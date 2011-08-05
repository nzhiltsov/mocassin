package unittest;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import unittest.info.PredictedPairInfo;

import com.google.inject.Inject;
@Ignore("references should be read from a store")	
public class StructuralElementTypeAnalyzerTest extends AbstractAnalyzerTest {
	private static final String TYPE_OUTPUT_FILENAME = "/tmp/type-predictions.txt";
	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	@Test
	public void testAnalyze() throws IOException {
		/*Function<Reference, PredictedPairInfo> function = new Function<Reference, PredictedPairInfo>() {

			@Override
			public PredictedPairInfo apply(Reference reference) {
				MocassinOntologyClasses fromType = structuralElementTypeRecognizer
						.predict(reference.getFrom());
				MocassinOntologyClasses toType = structuralElementTypeRecognizer
						.predict(reference.getTo());

				PredictedPairInfo pair = new PredictedPairInfo(fromType, toType);
				pair.setReference(reference);
				return pair;
			}
		};

		Iterable<PredictedPairInfo> pairs = Iterables.transform(
				getReferences(), function);

		print(pairs, TYPE_OUTPUT_FILENAME);*/
	}

	protected void print(Iterable<PredictedPairInfo> locations,
			String outputPath) throws IOException {
		FileWriter writer = new FileWriter(outputPath);
		writer.write("filename id refid f t\n");
		for (PredictedPairInfo info : locations) {
			Reference ref = info.getReference();
			MocassinOntologyClasses fromType = info.getFromType();
			MocassinOntologyClasses toType = info.getToType();
			writer.write(String.format("%s %s %s\n", ref.toString(),
					fromType != null ? fromType.toString() : "null",
					toType != null ? toType.toString() : "null"));
		}
		writer.flush();
		writer.close();
	}
}
