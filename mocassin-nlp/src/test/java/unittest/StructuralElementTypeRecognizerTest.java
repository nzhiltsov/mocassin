package unittest;

import gate.Document;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class StructuralElementTypeRecognizerTest {

	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	private StructuralElement testElement;

	@Before
	public void init() throws Exception {
		List<String> ids = gateDocumentDAO.getDocumentIds();
		String foundId = null;
		for (String id : ids) {
			if (id.startsWith("f000008.tex")) {
				foundId = id;
			}
		}
		Assert.assertNotNull(foundId);
		Document document = gateDocumentDAO.load(foundId);
		testElement = structuralElementSearcher.findById(document, 3747);
		gateDocumentDAO.release(document);
	}

	@Test
	public void testPredict() {

		MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
				.predict(testElement);
		System.out.println(prediction);
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
	}

}
