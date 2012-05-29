package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import edu.uci.ics.jung.graph.Graph;
import gate.Document;
import gate.Factory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.WordAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.*;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ DocumentAnalyzerModule.class, LatexParserModule.class,
        OntologyTestModule.class, FullTextModule.class, GateModule.class,
        PdfParserModule.class })
public class WordAnalyzerTest {
    @Inject
    private WordAnalyzer wordAnalyzer;
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
    @InjectLogger
    private Logger logger;

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
    public void testExtractWordFeatures() throws GeneratePdfSummaryException, PdflatexCompilationException {
        Document document = prepareDoc("ivm18");
        Graph<StructuralElement, Reference> graph;
        List<String> words = new ArrayList<String>();
        words.add("систем");
        words.add("теорем");
        words.add("существ");

        try {
            graph = referenceSearcher.retrieveStructuralGraph(document,
                    "http://mathnet.ru/ivm18");

            List<WordFeatureInfo> features = wordAnalyzer.extractWordFeatures(graph);
            for (WordFeatureInfo info: features) {
                if (info.getElement().getId() == 2900) {
                    List<String> testWords = info.getWords();
                    Collections.sort(testWords);
                    Collections.sort(words);
                    Assert.assertEquals("The list of word features is not equal to the expected one",
                            words, testWords);
                }
            }
        } finally {
            if (document != null) {
                Factory.deleteResource(document);
            }
        }
    }

    @Test
    public void generateWordRules() throws IOException, GeneratePdfSummaryException, PdflatexCompilationException {
        String[] docs = {"ivm101","ivm167","ivm170","ivm991","ivm260","ivm26","ivm3","ivm521","ivm829","ivm940"};
        Graph<StructuralElement, Reference> graph;

        FileWriter fstream = new FileWriter("/tmp/words.db");
        BufferedWriter out = new BufferedWriter(fstream);

        for (String doc: docs) {
            Document document = prepareDoc(doc);

            try {
                graph = referenceSearcher.retrieveStructuralGraph(document, "http://mathnet.ru/" + doc);

                List<WordFeatureInfo> features = wordAnalyzer.extractWordFeatures(graph);
                for (WordFeatureInfo info: features) {
                    List<String> words = info.getWords();
                    for (String word: words) {
                        out.write("HasWord(\"" + word + "\"," + doc + "_" + info.getElement().getId() + ")\n");
                    }
                }
            } finally {
                if (document != null) {
                    Factory.deleteResource(document);
                }
            }
        }

        out.close();
    }
}
