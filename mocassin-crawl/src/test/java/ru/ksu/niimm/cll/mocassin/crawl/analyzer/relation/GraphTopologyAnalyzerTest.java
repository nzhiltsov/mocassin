package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.GeneratePdfSummaryException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;
import gate.Document;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class, GateModule.class,
	PdfParserModule.class })
public class GraphTopologyAnalyzerTest {
    @Inject
    private GraphTopologyAnalyzer graphTopologyAnalyzer;

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

    private Graph<StructuralElement, Reference> graph;

    @Before
    public void init() throws Exception {
	Document document = prepareDoc("ivm18");
	graph = referenceSearcher.retrieveStructuralGraph(document,
		"http://mathnet.ru/ivm18");
    }

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
    public void testExtractCandidateFeatures() {
	int n = graph.getVertexCount();
	List<RelationFeatureInfo> candidates = graphTopologyAnalyzer
		.extractCandidateFeatures(graph);
	Assert.assertEquals(
		"The number of candidates must be equal to the complete graph cardinality, i.e. n(n-1)/2",
		n * (n - 1) / 2, candidates.size());
	checkNeighborJaccard(candidates, 19, 1017, 0.04347826f);
	checkNeighborJaccard(candidates, 2900, 3460, 0.5f);
	checkPreferentialAttachment(candidates, 2900, 3460, 9);
	checkPreferentialAttachment(candidates, 19, 1017, 119);
	checkPageRanks(candidates);
    }

    private void checkPageRanks(List<RelationFeatureInfo> candidates) {
	Map<StructuralElement, Float> element2PR = new HashMap<StructuralElement, Float>();
	for (RelationFeatureInfo info : candidates) {
	    if (!element2PR.containsKey(info.getFrom())) {
		element2PR.put(info.getFrom(), info.getFromPR());
	    }
	    if (!element2PR.containsKey(info.getTo())) {
		element2PR.put(info.getTo(), info.getToPR());
	    }
	}
	List<Entry<StructuralElement, Float>> list = new ArrayList<Entry<StructuralElement, Float>>(
		element2PR.entrySet());
	Collections.sort(list,
		new Comparator<Entry<StructuralElement, Float>>() {
		    public int compare(Entry<StructuralElement, Float> first,
			    Entry<StructuralElement, Float> second) {
			return first.getValue().compareTo(second.getValue());
		    }
		});
	Assert.assertEquals(
		"The highest PageRank is not equal to the expected one.",
		0.0621076, list.get(list.size() - 1).getValue(), 1e-4);
	Assert.assertEquals(
		"The element id with 2nd PageRank is not equal to the expected one.",
		5498, list.get(list.size() - 2).getKey().getId());
	Assert.assertEquals(
		"The element id with 3rd PageRank is not equal to the expected one.",
		5088, list.get(list.size() - 3).getKey().getId());
	Assert.assertEquals(
		"The element id with 4th PageRank is not equal to the expected one.",
		4860, list.get(list.size() - 4).getKey().getId());
	Assert.assertEquals(
		"The element id with highest PageRank is not equal to the expected one.",
		4808, list.get(list.size() - 5).getKey().getId());

    }

    private void checkPreferentialAttachment(
	    List<RelationFeatureInfo> candidates, int firstId, int secondId,
	    int expectedScore) {
	boolean foundRelation = false;

	for (RelationFeatureInfo info : candidates) {
	    foundRelation = (info.getFrom().getId() == firstId && info.getTo()
		    .getId() == secondId)
		    || (info.getFrom().getId() == secondId && info.getTo()
			    .getId() == firstId);

	    if (foundRelation) {
		Assert.assertEquals(
			"The preferential attachment score for the given two elements is not equal to the expected one.",
			expectedScore, info.getPreferentialAttachmentScore());
		break;
	    }

	}
	Assert.assertTrue(
		"Failed to find a relation between two elements with given ids.",
		foundRelation);
    }

    private void checkNeighborJaccard(List<RelationFeatureInfo> candidates,
	    int firstId, int secondId, float expectedJaccardValue) {
	boolean foundRelation = false;

	for (RelationFeatureInfo info : candidates) {
	    foundRelation = (info.getFrom().getId() == firstId && info.getTo()
		    .getId() == secondId)
		    || (info.getFrom().getId() == secondId && info.getTo()
			    .getId() == firstId);

	    if (foundRelation) {
		Assert.assertEquals(
			"The neighbor Jaccard coefficient for the given two elements is not equal to the expected one.",
			expectedJaccardValue,
			info.getNeighborJaccardCoefficient(), 1e-8);
		break;
	    }

	}
	Assert.assertTrue(
		"Failed to find a relation between two elements with given ids.",
		foundRelation);
    }
}
