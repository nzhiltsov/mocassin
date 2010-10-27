package unittest;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.analyzer.indexers.WeightedIndex;
import ru.ksu.niimm.cll.mocassin.analyzer.pos.VerbBasedFeatureAnalyzer;

import com.google.inject.Inject;

public class VerbBasedFeatureAnalyzerTest extends AbstractAnalyzerTest {
	private static final String TERMS_OUTPUT_FILENAME = "/tmp/verb-terms.txt";
	private static final String REF_VECTORS_OUTPUT_FILENAME = "/tmp/verb-features.txt";
	@Inject
	private VerbBasedFeatureAnalyzer verbBasedFeatureAnalyzer;

	@Test
	public void testBuildReferenceIndex() throws IOException {
		WeightedIndex index = getVerbBasedFeatureAnalyzer()
				.buildReferenceIndex(getReferences());
		print(index.getTerms(), TERMS_OUTPUT_FILENAME);
		String header = makeHeader(index.getTerms().size());
		print(index.getReferenceVectors(), REF_VECTORS_OUTPUT_FILENAME, header);
	}

	private String makeHeader(int n) {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= n; i++) {
			sb.append(String.format("v%d ", i));
		}
		return String.format("filename id refid %s", sb.toString());
	}

	public VerbBasedFeatureAnalyzer getVerbBasedFeatureAnalyzer() {
		return verbBasedFeatureAnalyzer;
	}

	protected void print(Iterable<String> terms, String outputPath)
			throws IOException {
		FileWriter writer = new FileWriter(outputPath);
		for (String term : terms) {
			writer.write(String.format("%s\n", term));
		}
		writer.flush();
		writer.close();
	}
}
