package ru.ksu.niimm.cll.mocassin.nlp;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.gate.ProcessException;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, GateModule.class, LatexParserModule.class,
		PdfParserModule.class })
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
			foundHasPartInstance = graph.getSource(ref).getId() == 4860
					&& graph.getDest(ref).getId() == 4965;
			if (foundHasPartInstance) {
				break;
			}
		}

		Assert.assertTrue("The 'hasPart' instance hasn't been found.",
				foundHasPartInstance);

		boolean foundTableInstance = false;
		Collection<StructuralElement> elements = graph.getVertices();
		for (StructuralElement element : elements) {
			foundTableInstance = element.getId() == 5632
					&& element.getPredictedClass() == MocassinOntologyClasses.TABLE;
			if (foundTableInstance) {
				break;
			}
		}
	}

}
