package unittest;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ReferenceElementLocationAnalyzerTest extends AbstractAnalyzerTest {
	private static final String LOCATIONS_OUTPUT_FILENAME = "/tmp/refelement-distances.txt";
	@Inject
	private ReferenceElementLocationAnalyzer referenceElementLocationAnalyzer;

	@Test
	public void testAnalyze() throws IOException {
		Function<Reference, ReferenceElementLocationInfo> function = new Function<Reference, ReferenceElementLocationInfo>() {

			@Override
			public ReferenceElementLocationInfo apply(Reference reference) {
				return getReferenceElementLocationAnalyzer().analyze(reference);
			}
		};

		Iterable<ReferenceElementLocationInfo> locations = Iterables.transform(
				getReferences(), function);

		print(locations, LOCATIONS_OUTPUT_FILENAME);
	}

	public ReferenceElementLocationAnalyzer getReferenceElementLocationAnalyzer() {
		return referenceElementLocationAnalyzer;
	}

	protected void print(Iterable<ReferenceElementLocationInfo> locations,
			String outputPath) throws IOException {
		FileWriter writer = new FileWriter(outputPath);
		for (ReferenceElementLocationInfo info : locations) {
			writer.write(String
					.format("%s %f %f\n", info.getReference().toString(), info
							.getStartDistance(), info.getEndDistance()));
		}
		writer.flush();
		writer.close();
	}
}
