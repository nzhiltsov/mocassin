package unittest;

import gate.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypesInfo;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class StructuralElementTypeRecognizerTest extends AbstractAnalyzerTest {
	private static final String TYPES_OUTPUT_FILENAME = "/tmp/type-similarities.txt";

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
	public void testRecognize() throws IOException {
		Function<Reference, StructuralElementTypesInfo> function = new Function<Reference, StructuralElementTypesInfo>() {

			@Override
			public StructuralElementTypesInfo apply(Reference reference) {
				return getStructuralElementTypeRecognizer()
						.recognize(reference);
			}
		};
		Iterable<StructuralElementTypesInfo> types = Iterables.transform(
				getReferences(), function);
		print(types, TYPES_OUTPUT_FILENAME);
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

	protected void print(Iterable<StructuralElementTypesInfo> types,
			String outputPath) throws IOException {
		FileWriter writer = new FileWriter(outputPath);
		for (StructuralElementTypesInfo info : types) {
			String fromStr = convertToString(info.getFromElementVector());
			String toStr = convertToString(info.getToElementVector());
			writer.write(String.format("%s %s%s\n", info.getReference()
					.toString(), fromStr, toStr));
		}
		writer.flush();
		writer.close();
	}
}
