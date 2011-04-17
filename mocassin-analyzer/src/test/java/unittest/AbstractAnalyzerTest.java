package unittest;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
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
public abstract class AbstractAnalyzerTest {
	private static final String REF_CONTEXT_DATA_INPUT_FOLDER = "/tmp/refcontexts-data";

	@Inject
	private Logger logger;

	private List<Reference> references;

	@Before
	public void init() throws Exception {
		File dir = new File(REF_CONTEXT_DATA_INPUT_FOLDER);
		File[] files = dir.listFiles();
		this.references = new ArrayList<Reference>();
		for (File file : files) {
			List<Reference> refs;
			try {
				refs = ReferenceFeatureReader.read(new FileReader(file));
				this.references.addAll(refs);
			} catch (Exception e) {
				logger.log(Level.SEVERE, String.format(
						"failed to parse file: %s", file.getName()));
			}

		}
	}

	protected <T> void print(Map<T, Vector> map, String outputPath,
			String header) throws IOException {
		FileWriter writer = new FileWriter(outputPath);

		if (header != null) {
			writer.write(String.format("%s\n", header));
		}
		for (Entry<T, Vector> t : map.entrySet()) {
			Vector vector = t.getValue();
			String vectorStr = convertToString(vector);
			writer.write(String.format("%s %s\n", t.toString(), vectorStr));
		}
		writer.flush();
		writer.close();
	}

	protected String convertToString(Vector vector) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vector.numDimensions(); i++) {
			double value = vector.value(i);
			sb.append((double) Math.round(value * 1000) / 1000);
			sb.append(" ");
		}
		String vectorStr = sb.toString();
		return vectorStr;
	}

	protected String convertToString(SortedMap<String, Float> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Float> entry : map.entrySet()) {
			float value = entry.getValue();
			sb.append((float) Math.round(value * 1000) / 1000);
			sb.append(" ");
		}
		return sb.toString();
	}

	protected List<Reference> getReferences() {
		return references;
	}
}
