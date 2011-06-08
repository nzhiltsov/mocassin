package unittest.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.classifier.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.analyzer.classifier.Prediction;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyModule.class, VirtuosoModule.class,
		FullTextModule.class })
public class NavigationalRelationClassifierTest {
	@Inject
	private NavigationalRelationClassifier navigationalRelationClassifier;
	@Inject
	private ReferenceSearcher referenceSearcher;

	private final List<Reference> references = new ArrayList<Reference>();
	private Graph<StructuralElement, Reference> graph;

	@Before
	public void init() throws Exception {
		ParsedDocument document = new ParsedDocumentImpl("math/0205003", "http://arxiv.org/abs/math/0205003",
				"http://arxiv.org/pdf/math/0205003");
		graph = this.referenceSearcher.retrieveStructuralGraph(document);
		Collection<Reference> edges = graph.getEdges();
		Assert.assertTrue(edges.size() > 0);

		for (Reference ref : edges) {
			if (ref.getPredictedRelation() == null) {
				this.references.add(ref);
			}

		}
	}

	@Test
	public void testPredict() {
		for (Reference reference : references) {
			Prediction prediction = navigationalRelationClassifier.predict(
					reference, graph);
			Assert.assertNotNull(prediction);
			System.out.println(reference.getAdditionalRefid() + " "
					+ prediction.getRelation() + " " + prediction.getConfidence());
		}

	}
}
