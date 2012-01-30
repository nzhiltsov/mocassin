package unittest.relations;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
		OntologyModule.class, VirtuosoModule.class, FullTextModule.class,
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
