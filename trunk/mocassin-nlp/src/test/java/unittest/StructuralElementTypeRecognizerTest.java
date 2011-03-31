package unittest;

import gate.Document;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.inject.Inject;
import com.google.inject.internal.Maps;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class StructuralElementTypeRecognizerTest {
	private static final String CORPUS_ELEMENT_INDEX_OUTPUT_DIR = "/tmp/corpus-element-index";
	private static final String PREDICTED_ELEMENTS_OUTPUT_DIR = String.format(
			"%s/%s", CORPUS_ELEMENT_INDEX_OUTPUT_DIR, "predicted");
	private static final String UNPREDICTED_ELEMENTS_OUTPUT_DIR = String
			.format("%s/%s", CORPUS_ELEMENT_INDEX_OUTPUT_DIR, "unpredicted");
	@Inject
	private Logger logger;
	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	private StructuralElement testElement;

	private List<String> documentIds;

	@Before
	public void init() throws Exception {
		documentIds = gateDocumentDAO.getDocumentIds();
		String foundId = null;
		for (String id : documentIds) {
			if (id.startsWith("f000008.tex")) {
				foundId = id;
				break;
			}
		}
		Assert.assertNotNull(foundId);
		Document document = gateDocumentDAO.load(foundId);
		testElement = structuralElementSearcher.findById(document, 3747);
		gateDocumentDAO.release(document);

		makeRootDir(CORPUS_ELEMENT_INDEX_OUTPUT_DIR);
		makeRootDir(PREDICTED_ELEMENTS_OUTPUT_DIR);
		makeRootDir(UNPREDICTED_ELEMENTS_OUTPUT_DIR);
	}

	@Test
	public void testPredict() {

		MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
				.predict(testElement);
		System.out.println(prediction);
	}

	@Test
	public void testPredictForCorpus() throws Exception {
		List<String> sample = CollectionUtil.sampleRandomSublist(documentIds, 30);
		for (String id : sample) {
			Document doc = null;
			try {
				doc = getGateDocumentDAO().load(id);
				List<StructuralElement> elements = getStructuralElementSearcher()
						.retrieve(doc);
				process(elements, id);

			} catch (Exception e) {
				logger.log(Level.SEVERE, String.format(
						"couldn't load the document: %s", id));
			}
			if (doc != null) {
				getGateDocumentDAO().release(doc);
				doc = null;
			}
		}
	}

	private void process(List<StructuralElement> elements, String documentId) {
		Map<StructuralElement, MocassinOntologyClasses> predictedMap = Maps
				.newHashMap();
		List<StructuralElement> unpredictedElements = new ArrayList<StructuralElement>();
		for (StructuralElement element : elements) {
			MocassinOntologyClasses predictedClass = getStructuralElementTypeRecognizer()
					.predict(element);
			if (predictedClass != null) {
				predictedMap.put(element, predictedClass);
			} else {
				unpredictedElements.add(element);
			}

		}

		if (!predictedMap.isEmpty()) {
			save(predictedMap.entrySet(), documentId);
		}
		if (!unpredictedElements.isEmpty()) {
			save(unpredictedElements, documentId);
		}
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	private void save(List<StructuralElement> elements, String documentId) {
		String filename = documentId.substring(0, documentId.indexOf("."));
		try {
			FileWriter writer = new FileWriter(String.format("%s/%s.txt",
					UNPREDICTED_ELEMENTS_OUTPUT_DIR, filename));
			writer.write("id|title|name|prediction\n");
			for (StructuralElement element : elements) {
				writer.write(String.format("%d|%s|%s|null\n", element.getId(),
						element.toTitleString(), element.getName()));
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format(
					"couldn't save the file with name: %s", filename));
		}
	}

	private void save(
			Set<Entry<StructuralElement, MocassinOntologyClasses>> entrySet,
			String documentId) {
		String filename = documentId.substring(0, documentId.indexOf("."));
		try {
			FileWriter writer = new FileWriter(String.format("%s/%s.txt",
					PREDICTED_ELEMENTS_OUTPUT_DIR, filename));
			writer.write("id|title|name|prediction\n");
			for (Entry<StructuralElement, MocassinOntologyClasses> entry : entrySet) {
				StructuralElement element = entry.getKey();
				MocassinOntologyClasses prediction = entry.getValue();
				writer.write(String.format("%d|%s|%s|%s\n", element.getId(),
						element.toTitleString(), element.getName(), prediction
								.toString()));
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format(
					"couldn't save the file with name: %s", filename));
		}
	}

	private void makeRootDir(String path) {
		File dir = new File(path);
		if (!dir.canRead()) {
			if (!dir.mkdir()) {
				throw new RuntimeException(
						String
								.format(
										"couldn't create root folder to save the corpus element index with following path: %s",
										path));
			}
		}
	}
}