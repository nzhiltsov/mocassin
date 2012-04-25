package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;

import static java.lang.String.format;
import static ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElementImpl.IdComparator;

import com.google.common.collect.ImmutableMap;
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
	/**
	 * values should be added according to ascending order of the ids
	 */
	private static Map<Integer, Integer> id2pagenumber = new ImmutableMap.Builder<Integer, Integer>()
			.put(19, 1).put(2900, 4).build();

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
	public void testPageNumbers() {
		List<StructuralElement> elements = getStructuralElementSearcher()
				.retrieveElements(parsedDocument);
		Collections.sort(elements, new IdComparator());
		Iterator<StructuralElement> iterator = elements.iterator();
		int checkedNumber = 0;
		while (iterator.hasNext()) {
			StructuralElement element = iterator.next();
			for (Map.Entry<Integer, Integer> entry : id2pagenumber.entrySet()) {

				if (element.getId() == entry.getKey()) {
					Assert.assertEquals(
							format("The start page of the element='%s' does not equal to the expected one",
									element.getUri()), entry.getValue()
									.intValue(), element.getStartPageNumber());
					checkedNumber++;
				}
			}
		}
		Assert.assertEquals(
				"Number of checked elements does not equal to the expected one",
				id2pagenumber.entrySet().size(), checkedNumber);
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
