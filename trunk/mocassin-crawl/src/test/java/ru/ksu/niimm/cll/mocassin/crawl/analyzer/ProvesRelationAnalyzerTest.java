package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ProvesRelationAnalyzer;
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
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyTestModule.class,
		FullTextModule.class, GateModule.class, PdfParserModule.class })
public class ProvesRelationAnalyzerTest {
	@Inject
	ProvesRelationAnalyzer provesRelationAnalyzer;
	@Inject
	private ReferenceSearcher referenceSearcher;

	private Graph<StructuralElement, Reference> graph;

	private ParsedDocument document;

	@Before
	public void init() {
		document = new ParsedDocumentImpl("ivm537",
				"http://mathnet.ru/ivm537",
				"http://mathnet.ru/ivm537");
		graph = this.referenceSearcher.retrieveStructuralGraph(document);
	}

	@Test
	public void testAnalyze() throws IOException {
		this.provesRelationAnalyzer.addRelations(graph, document);
		Collection<Reference> edges = graph.getEdges();
		boolean found = false;
		for (Reference ref : edges) {
			StructuralElement from = graph.getSource(ref);
			if (ref.getPredictedRelation() == MocassinOntologyRelations.PROVES && from.getId() == 1082) {
				StructuralElement to = graph.getDest(ref);
				Assert.assertEquals(897, to.getId());
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);
	}
}
