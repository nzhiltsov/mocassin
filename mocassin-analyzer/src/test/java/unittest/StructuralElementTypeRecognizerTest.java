package unittest;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypesInfo;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class StructuralElementTypeRecognizerTest extends AbstractAnalyzerTest {
	private static final String TYPES_OUTPUT_FILENAME = "/tmp/type-similarities.txt";

	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

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
