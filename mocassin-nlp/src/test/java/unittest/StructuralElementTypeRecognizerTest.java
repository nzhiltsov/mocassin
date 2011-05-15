package unittest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, OntologyModule.class, VirtuosoModule.class,
		LatexParserModule.class, FullTextModule.class })
public class StructuralElementTypeRecognizerTest {

	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	@Inject
	private StructuralElementSearcher structuralElementSearcher;

	private StructuralElement testElement;

	@Before
	public void init() throws Exception {

		ParsedDocument parsedDocument = new ParsedDocumentImpl("math/0205003",
				"http://arxiv.org/abs/math/0205003",
				"http://arxiv.org/pdf/math/0205003");
		testElement = structuralElementSearcher.findById(parsedDocument, 402);

	}

	@Test
	public void testPredict() {

		MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
				.predict(testElement);
		Assert.assertEquals(MocassinOntologyClasses.THEOREM, prediction);
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
	}

}
