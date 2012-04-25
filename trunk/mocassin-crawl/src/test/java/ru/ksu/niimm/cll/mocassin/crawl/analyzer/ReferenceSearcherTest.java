package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ProcessException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
		OntologyTestModule.class, FullTextModule.class, GateModule.class,
		PdfParserModule.class })
public class ReferenceSearcherTest {
	@Inject
	private ReferenceSearcher referenceSearcher;
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	private ParsedDocument firstDocument;

	private ParsedDocument secondDocument;

	@Before
	public void init() throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException {
		firstDocument = new ParsedDocumentImpl("ivm18",
				"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18");
		gateProcessingFacade.process(firstDocument.getCollectionId());
		secondDocument = new ParsedDocumentImpl("ivm537",
				"http://mathnet.ru/ivm537", "http://mathnet.ru/ivm537");
		gateProcessingFacade.process(secondDocument.getCollectionId());
	}

	@Test
	public void testRetrieveReferences() throws AccessGateDocumentException,
			IOException, AccessGateStorageException, ProcessException {
		checkFirstDocument();
		checkSecondDocument();
	}

	private void checkFirstDocument() {
		Graph<StructuralElement, Reference> graph = this.referenceSearcher
				.retrieveStructuralGraph(firstDocument);
		Collection<Reference> edges = graph.getEdges();
		Assert.assertTrue("The reference list is empty", edges.size() > 0);

		boolean foundHasPartInstance = false;
		for (Reference ref : edges) {
			foundHasPartInstance = graph.getSource(ref).getId() == 1017
					&& graph.getDest(ref).getId() == 2900;
			if (foundHasPartInstance) {
				break;
			}
		}

		Assert.assertTrue("The 'hasPart' instance hasn't been found.",
				foundHasPartInstance);

		int tableCount = 0;
		Collection<StructuralElement> elements = graph.getVertices();
		for (StructuralElement element : elements) {
			if (element.getPredictedClass() == MocassinOntologyClasses.TABLE) {
				tableCount++;
			}
		}
		Assert.assertEquals(
				"The number of tables in the document does not equal to the expected one.",
				3, tableCount);

		boolean foundFollowedByInstance = false;
		for (Reference ref : edges) {
			foundFollowedByInstance = graph.getSource(ref).getId() == 19
					&& graph.getDest(ref).getId() == 1017
					&& ref.getPredictedRelation() == MocassinOntologyRelations.FOLLOWED_BY;
			if (foundFollowedByInstance) {
				break;
			}
		}
		Assert.assertTrue("The 'followedBy' instance hasn't been found.",
				foundFollowedByInstance);
	}

	private void checkSecondDocument() {
		Graph<StructuralElement, Reference> graph = this.referenceSearcher
				.retrieveStructuralGraph(secondDocument);
		Collection<Reference> edges = graph.getEdges();
		Assert.assertTrue("The reference list is empty", edges.size() > 0);

		boolean foundFollowedByInstance = false;
		boolean foundFalseFollowedByInstance = false;
		for (Reference ref : edges) {
			foundFollowedByInstance = graph.getSource(ref).getId() == 897
					&& graph.getDest(ref).getId() == 1082
					&& ref.getPredictedRelation() == MocassinOntologyRelations.FOLLOWED_BY;
			foundFalseFollowedByInstance = graph.getSource(ref).getId() == 1082
					&& graph.getDest(ref).getId() == 2359
					&& ref.getPredictedRelation() == MocassinOntologyRelations.FOLLOWED_BY;
			if (foundFalseFollowedByInstance) {
				Assert.fail("Found false 'followedBy' instance. There's a section text between structural elements.");
			}
			if (foundFollowedByInstance) {
				break;
			}
		}
		Assert.assertTrue("The 'followedBy' instance hasn't been found.",
				foundFollowedByInstance);
	}

}
