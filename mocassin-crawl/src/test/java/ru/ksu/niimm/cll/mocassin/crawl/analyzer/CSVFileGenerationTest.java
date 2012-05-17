package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import edu.uci.ics.jung.graph.Graph;
import gate.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.*;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ DocumentAnalyzerModule.class, LatexParserModule.class,
        OntologyTestModule.class, FullTextModule.class, GateModule.class,
        PdfParserModule.class })
public class CSVFileGenerationTest {
    @Inject
    private ReferenceSearcher referenceSearcher;
    @Inject
    private GateProcessingFacade gateProcessingFacade;
    @Inject
    private LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;
    @Inject
    private PdflatexWrapper pdflatexWrapper;
    @Inject
    private Latex2PDFMapper latex2pdfMapper;
    @Inject
    private ArxmlivProducer arxmlivProducer;
    @Inject
    private OntologyFacade ontologyFacade;
    @Inject
    private HashMap<Pair<MocassinOntologyClasses, MocassinOntologyClasses>, Boolean> cacheMap;

    private Document prepareDoc(String documentId)
            throws PdflatexCompilationException, GeneratePdfSummaryException {
	    latexDocumentHeaderPatcher.patch(documentId);
	    pdflatexWrapper.compilePatched(documentId);
	    latex2pdfMapper.generateSummary(documentId);
	    String arxmlivFilePath = arxmlivProducer.produce(documentId);
        return gateProcessingFacade.process(documentId, new File(
                arxmlivFilePath), "utf8");
    }

    @Test
    public void generateFile() throws Exception {
        String[] documents = {"ivm101", "ivm170", "ivm260", "ivm3",  "ivm521",
                              "ivm829", "ivm167", "ivm180", "ivm26", "ivm940"};

        FileWriter fstream = new FileWriter("/tmp/relations.csv");
        BufferedWriter out = new BufferedWriter(fstream);

        out.write("doc_id\tfrom_elem_id\tto_elem_id\trelation\tinverse\n");

        for (String doc : documents) {
            processDocument(doc, out);
        }

        out.close();
    }

    private void processDocument(String documentId, BufferedWriter out) throws Exception {
        Document document = prepareDoc(documentId);
        Graph<StructuralElement, Reference> graph =
                referenceSearcher.retrieveStructuralGraph(document, "http://mathnet.ru/" + documentId);

        ArrayList<StructuralElement> elements = new ArrayList<StructuralElement>(
                graph.getVertices());
        int size = elements.size();

        StructuralElement from,to;

        for (int i = 0; i < size - 1; i++) {
            from = elements.get(i);
            for (int j = i + 1; j < size; j++) {
                to = elements.get(j);

                if (isZeroElement(from, to) || isPartOf(from, to, graph)
                        || isUnknownEquation(from, to) || isThereNoValidRelation(from, to)) {
                    continue;
                }

                out.write(documentId + "\t" + from.getId() + "\t" + to.getId() + "\n");
            }
        }
    }

    private boolean isZeroElement(StructuralElement from, StructuralElement to) {
        return (from.getId() == 0 || to.getId() == 0);
    }

    private boolean isPartOf(StructuralElement from, StructuralElement to, Graph<StructuralElement, Reference> graph) {
        for (Reference ref : graph.findEdgeSet(from, to)) {
            if (ref.getPredictedRelation() == MocassinOntologyRelations.HAS_PART) {
                return true;
            }
        }

        for (Reference ref : graph.findEdgeSet(to, from)) {
            if (ref.getPredictedRelation() == MocassinOntologyRelations.HAS_PART) {
                return true;
            }
        }

        return false;
    }

    private boolean isUnknownEquation(StructuralElement from, StructuralElement to) {
        if (to.getPredictedClass() == MocassinOntologyClasses.EQUATION
                && from.getPredictedClass() != MocassinOntologyClasses.EQUATION) {
            return from.getGateEndOffset() < to.getGateStartOffset();
        }

        if (from.getPredictedClass() == MocassinOntologyClasses.EQUATION
                && to.getPredictedClass() != MocassinOntologyClasses.EQUATION) {
            return to.getGateEndOffset() < from.getGateStartOffset();
        }

        return false;
    }

    private boolean isThereNoValidRelation(StructuralElement from, StructuralElement to) {
        Pair<MocassinOntologyClasses, MocassinOntologyClasses> pair =
                new Pair<MocassinOntologyClasses, MocassinOntologyClasses>
                        (from.getPredictedClass(), to.getPredictedClass());

        if (cacheMap.containsKey(pair)) return cacheMap.get(pair);

        boolean result = !isThereRelationsThatNeeded(ontologyFacade.getRelations(from.getPredictedClass(),
                to.getPredictedClass()))
                && !isThereRelationsThatNeeded(ontologyFacade.getRelations(from.getPredictedClass(),
                to.getPredictedClass()));

        cacheMap.put(pair, result);

        return result;
    }

    private boolean isThereRelationsThatNeeded(List<MocassinOntologyRelations> list) {
        return list.contains(MocassinOntologyRelations.DEPENDS_ON)
                || list.contains(MocassinOntologyRelations.EXEMPLIFIES)
                || list.contains(MocassinOntologyRelations.HAS_CONSEQUENCE)
                || list.contains(MocassinOntologyRelations.PROVES)
                || list.contains(MocassinOntologyRelations.REFERS_TO);
    }
}
