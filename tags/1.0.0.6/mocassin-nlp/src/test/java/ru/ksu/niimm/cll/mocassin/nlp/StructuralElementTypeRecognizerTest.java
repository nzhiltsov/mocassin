package ru.ksu.niimm.cll.mocassin.nlp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, GateModule.class, LatexParserModule.class,
		PdfParserModule.class })
public class StructuralElementTypeRecognizerTest {

	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	@Inject
	private StructuralElementSearcher structuralElementSearcher;

	@Inject
	private GateProcessingFacade gateProcessingFacade;

	private StructuralElement testProofElement;

	private StructuralElement testTableElement;

	private StructuralElement rootElement;

	@Before
	public void init() throws Exception {

		ParsedDocument parsedDocument = new ParsedDocumentImpl("ivm537",
				"http://mathnet.ru/ivm537", "http://mathnet.ru/ivm537");
		gateProcessingFacade.process(parsedDocument.getCollectionId());
		testProofElement = structuralElementSearcher.findById(parsedDocument,
				1082);
		rootElement = structuralElementSearcher.findById(parsedDocument, 0);

		parsedDocument = new ParsedDocumentImpl("ivm18",
				"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18");
		gateProcessingFacade.process(parsedDocument.getCollectionId());
		testTableElement = structuralElementSearcher.findById(parsedDocument,
				5499);
	}

	@Test
	public void testRootElementType() {
		MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
				.predict(rootElement);
		Assert.assertEquals(
				"The type of root element does not equal to the expected one.",
				MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT,
				prediction);
	}

	@Test
	public void testPredictProof() {

		MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
				.predict(testProofElement);
		Assert.assertEquals(MocassinOntologyClasses.PROOF, prediction);
	}

	@Test
	public void testPredictTable() {

		MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
				.predict(testTableElement);
		Assert.assertEquals(MocassinOntologyClasses.TABLE, prediction);
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
	}

}
