package ru.ksu.niimm.cll.mocassin.nlp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, GateModule.class, LatexParserModule.class,
		PdfParserModule.class })
public class StructuralElementSearcherTest {

	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	private ParsedDocument parsedDocument;

	@Before
	public void init() throws Exception {
		this.parsedDocument = new ParsedDocumentImpl("ivm18",
				"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18");
		gateProcessingFacade.process(parsedDocument.getCollectionId());
	}

	@Test
	public void testFindById() throws Exception {

		StructuralElement foundElement = getStructuralElementSearcher()
				.findById(parsedDocument, 5499);
		Assert.assertNotNull("The found element is null", foundElement);

	}

	@Test
	public void testFindClosestPredecessor() {
		MocassinOntologyClasses[] hasConsequenceDomains = MocassinOntologyRelations
				.getValidDomains(MocassinOntologyRelations.HAS_CONSEQUENCE);
		StructuralElement predecessor = getStructuralElementSearcher()
				.findClosestPredecessor(parsedDocument, 5499,
						hasConsequenceDomains);
		Assert.assertEquals(
				"The found predecessor id does not equal to the expected one.",
				3460, predecessor.getId());
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

}
