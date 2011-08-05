package unittest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { NlpModule.class, OntologyModule.class, VirtuosoModule.class,
		LatexParserModule.class, FullTextModule.class })
public class StructuralElementSearcherTest {

	@Inject
	private StructuralElementSearcher structuralElementSearcher;

	private ParsedDocument parsedDocument;

	@Before
	public void init() throws Exception {
		this.parsedDocument = new ParsedDocumentImpl("math/0205003", "http://arxiv.org/abs/math/0205003",
		"http://arxiv.org/pdf/math/0205003");
	}

	@Test
	public void testFindById() throws Exception {

		StructuralElement foundElement = getStructuralElementSearcher()
				.findById(parsedDocument, 1167);
		Assert.assertEquals("Lemma 2.2.", foundElement.getTitle());
	}

	@Test
	public void testFindClosestPredecessor() {
		MocassinOntologyClasses[] hasConsequenceDomains = MocassinOntologyRelations
				.getValidDomains(MocassinOntologyRelations.HAS_CONSEQUENCE);
		StructuralElement predecessor = getStructuralElementSearcher()
				.findClosestPredecessor(parsedDocument, 2949,
						hasConsequenceDomains);
		Assert.assertEquals(1167, predecessor.getId());
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}


}