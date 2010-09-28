package unittest;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
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
	@Inject
	private LatentSemanticIndexer latentSemanticIndexer;

	private List<Reference> references;

	@Before
	public void init() throws Exception {
		File dir = new File("/tmp/refcontexts-data");
		File[] files = dir.listFiles();
		this.references = new ArrayList<Reference>();
		for (File file : files) {
			List<Reference> refs = ReferenceFeatureReader.read(new FileReader(
					file));
			this.references.addAll(refs);
		}
	}

	@Test
	public void testReferenceBuildIndex() {
		Map<Reference, Vector> index = getLatentSemanticIndexer()
				.buildReferenceIndex(getReferences().subList(0, 100));
		printIndex(index);
	}

	private void printIndex(Map<Reference, Vector> index) {
		for (Reference ref : index.keySet()) {
			Vector vector = index.get(ref);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < vector.numDimensions(); i++) {
				double value = vector.value(i);
				sb.append((double) Math.round(value * 1000) / 1000);
				sb.append(" ");
			}
			System.out.println(String.format("%s:%s %s", ref.getDocumentName(),
					ref.getId(), sb.toString()));
		}
	}

	public LatentSemanticIndexer getLatentSemanticIndexer() {
		return latentSemanticIndexer;
	}

	public List<Reference> getReferences() {
		return references;
	}

}