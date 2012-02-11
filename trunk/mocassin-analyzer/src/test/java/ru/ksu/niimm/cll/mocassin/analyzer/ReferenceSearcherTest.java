package ru.ksu.niimm.cll.mocassin.analyzer;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.gate.ProcessException;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, VirtuosoModule.class, FullTextModule.class,
	GateModule.class, PdfParserModule.class })
public class ReferenceSearcherTest {
	@Inject
	private ReferenceSearcher referenceSearcher;
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	private ParsedDocument document;

	@Before
	public void init() throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException {
		document = new ParsedDocumentImpl("ivm18", "http://mathnet.ru/ivm18",
				"http://mathnet.ru/ivm18");
		gateProcessingFacade.process(document.getCollectionId());
	}

	@Test
	public void testRetrieveReferences() throws AccessGateDocumentException,
			IOException, AccessGateStorageException, ProcessException {
		Graph<StructuralElement, Reference> graph = this.referenceSearcher
				.retrieveStructuralGraph(document);
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
	}

}
