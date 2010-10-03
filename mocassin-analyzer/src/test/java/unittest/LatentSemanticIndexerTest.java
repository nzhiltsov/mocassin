package unittest;

import java.io.IOException;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LSIndex;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LatentSemanticIndexer;

import com.google.inject.Inject;

public class LatentSemanticIndexerTest extends AbstractAnalyzerTest {

	private static final String TERM_VECTORS_OUTPUT_FILENAME = "/tmp/lsi-terms.txt";

	private static final String REF_VECTORS_OUTPUT_FILENAME = "/tmp/lsi-refcontexts.txt";

	@Inject
	private LatentSemanticIndexer latentSemanticIndexer;

	@Test
	public void testReferenceBuildIndex() throws IOException {
		LSIndex index = getLatentSemanticIndexer().buildReferenceIndex(
				getReferences());
		print(index.getTermVectors(), TERM_VECTORS_OUTPUT_FILENAME);
		print(index.getReferenceVectors(), REF_VECTORS_OUTPUT_FILENAME);
	}

	public LatentSemanticIndexer getLatentSemanticIndexer() {
		return latentSemanticIndexer;
	}

}
