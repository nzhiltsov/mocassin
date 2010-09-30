package unittest;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LSIndex;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LatentSemanticIndexer;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceFeatureReader;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.aliasi.matrix.Vector;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyModule.class, VirtuosoModule.class })
public class LatentSemanticIndexerTest {
	private static final String REF_CONTEXT_DATA_INPUT_FOLDER = "/tmp/refcontexts-data";

	private static final String TERM_VECTORS_OUTPUT_FILENAME = "/tmp/lsi-terms.txt";

	private static final String REF_VECTORS_OUTPUT_FILENAME = "/tmp/lsi-refcontexts.txt";

	@Inject
	private LatentSemanticIndexer latentSemanticIndexer;

	private List<Reference> references;

	@Before
	public void init() throws Exception {
		File dir = new File(REF_CONTEXT_DATA_INPUT_FOLDER);
		File[] files = dir.listFiles();
		this.references = new ArrayList<Reference>();
		for (File file : files) {
			List<Reference> refs = ReferenceFeatureReader.read(new FileReader(
					file));
			this.references.addAll(refs);
		}
	}

	@Test
	public void testReferenceBuildIndex() throws IOException {
		LSIndex index = getLatentSemanticIndexer().buildReferenceIndex(
				getReferences());
		print(index.getTermVectors(), TERM_VECTORS_OUTPUT_FILENAME);
		print(index.getReferenceVectors(), REF_VECTORS_OUTPUT_FILENAME);
	}

	private <T> void print(Map<T, Vector> map, String outputPath)
			throws IOException {
		FileWriter writer = new FileWriter(outputPath);
		for (T t : map.keySet()) {
			Vector vector = map.get(t);
			String vectorStr = convertToString(vector);
			writer.write(String.format("%s %s\n", t.toString(), vectorStr));
		}
		writer.flush();
		writer.close();
	}

	private String convertToString(Vector vector) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vector.numDimensions(); i++) {
			double value = vector.value(i);
			sb.append((double) Math.round(value * 1000) / 1000);
			sb.append(" ");
		}
		String vectorStr = sb.toString();
		return vectorStr;
	}

	public LatentSemanticIndexer getLatentSemanticIndexer() {
		return latentSemanticIndexer;
	}

	public List<Reference> getReferences() {
		return references;
	}

}
