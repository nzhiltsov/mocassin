package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class,
		GateModule.class, PdfParserModule.class })
public class HasConsequenceRelationAnalyzerTest {
	@Inject
	private HasConsequenceRelationAnalyzer hasConsequenceRelationAnalyzer;
	@Inject
	private ReferenceSearcher referenceSearcher;

	private Graph<StructuralElement, Reference> graph;

	private ParsedDocument document;

	@Before
	public void init() {
		document = new ParsedDocumentImpl("ivm537", "http://mathnet.ru/ivm537",
				"http://mathnet.ru/ivm537");
		graph = this.referenceSearcher.retrieveStructuralGraph(document);
	}

	@Test
	public void testAnalyze() throws IOException {
		getHasConsequenceRelationAnalyzer().addRelations(graph, document);
		Collection<Reference> edges = graph.getEdges();
		boolean found = false;
		for (Reference ref : edges) {
			if (ref.getPredictedRelation() == MocassinOntologyRelations.HAS_CONSEQUENCE) {
				StructuralElement from = graph.getSource(ref);
				StructuralElement to = graph.getDest(ref);
				Assert.assertEquals(
						"The source id of the 'hasConsequence' relation does not equal to the expected one.",
						2359, from.getId());
				Assert.assertEquals(
						"The range id of the 'hasConsequence' relation does not equal to the expected one.",
						2926, to.getId());
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);
	}

	public HasConsequenceRelationAnalyzer getHasConsequenceRelationAnalyzer() {
		return hasConsequenceRelationAnalyzer;
	}

}
