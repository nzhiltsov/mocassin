package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class, GateModule.class,
	PdfParserModule.class })
public class GraphTopologyAnalyzerTest {
    @Inject
    private GraphTopologyAnalyzer graphTopologyAnalyzer;

    @Inject
    private ReferenceSearcher referenceSearcher;

    private Graph<StructuralElement, Reference> graph;

    @Before
    public void init() {
	graph = referenceSearcher
		.retrieveStructuralGraph(new ParsedDocumentImpl("ivm18",
			"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18"));
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
	checkPreferentialAttachment(candidates, 2900, 3460, 6);
	checkPreferentialAttachment(candidates, 19, 1017, 119);
	checkPageRanks(candidates, 19, 1017, 0.0160327601f, 0.0173156749f);
    }

    private void checkPageRanks(List<RelationFeatureInfo> candidates,
	    int firstId, int secondId, float expectedFirstPR,
	    float expectedSecondPR) {
	boolean foundRelation = false;

	for (RelationFeatureInfo info : candidates) {
	    foundRelation = (info.getFrom().getId() == firstId && info.getTo()
		    .getId() == secondId)
		    || (info.getFrom().getId() == secondId && info.getTo()
			    .getId() == firstId);

	    if (foundRelation) {
		Assert.assertEquals(
			"The PageRank of the first element is not equal to the expected one.",
			expectedFirstPR, info.getFromPR(), 1e-8);
		Assert.assertEquals(
			"The PageRank of the second element is not equal to the expected one.",
			expectedSecondPR, info.getToPR(), 1e-8);
		break;
	    }

	}
	Assert.assertTrue(
		"Failed to find a relation between two elements with given ids.",
		foundRelation);
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
