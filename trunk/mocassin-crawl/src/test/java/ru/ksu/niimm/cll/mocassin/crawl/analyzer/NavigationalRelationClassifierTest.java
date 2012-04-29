package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.Prediction;
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
public class NavigationalRelationClassifierTest {
	@Inject
	private NavigationalRelationClassifier navigationalRelationClassifier;
	@Inject
	private ReferenceSearcher referenceSearcher;

	private Graph<StructuralElement, Reference> graph;

	private Reference knownRefersToReference;

	private Reference knownDependsOnReference;

	@Before
	public void init() throws Exception {
		ParsedDocument document = new ParsedDocumentImpl("ivm18",
				"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18");
		graph = this.referenceSearcher.retrieveStructuralGraph(document);
		Collection<Reference> references = graph.getEdges();
		Assert.assertTrue(references.size() > 0);

		boolean foundFirst = false;

		boolean foundSecond = false;

		Iterator<Reference> it = references.iterator();

		while (it.hasNext() && !(foundFirst && foundSecond)) {
			Reference ref = it.next();
			if (ref.getId() == 5087) {
				this.knownRefersToReference = ref;
				foundFirst = true;
			} else if (ref.getId() == 4766) {
				this.knownDependsOnReference = ref;
				foundSecond = true;
			}
		}
		Assert.assertNotNull("The known 'refersTo' reference is null",
				this.knownRefersToReference);
		Assert.assertNotNull("The known 'dependsOn' reference is null",
				this.knownDependsOnReference);
	}

	@Test
	public void testPredictRefersTo() {
		Prediction prediction = navigationalRelationClassifier.predict(
				knownRefersToReference, graph);
		Assert.assertEquals("The predicted relation is not of the expected type.", MocassinOntologyRelations.REFERS_TO,
				prediction.getRelation());

	}

	@Test
	public void testPredictDependsOn() {
		Prediction prediction = navigationalRelationClassifier.predict(
				knownDependsOnReference, graph);
		Assert.assertEquals("The predicted relation is not of the expected type.", MocassinOntologyRelations.DEPENDS_ON,
				prediction.getRelation());
	}

}
