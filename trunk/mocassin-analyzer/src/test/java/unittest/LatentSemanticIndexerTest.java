package unittest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.aliasi.dict.Dictionary;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.TrieDictionary;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LatentSemanticIndexer;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceFeatureReader;
import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

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
		getLatentSemanticIndexer().buildReferenceIndex(
				getReferences().subList(0, 50));
	}

	public LatentSemanticIndexer getLatentSemanticIndexer() {
		return latentSemanticIndexer;
	}

	public List<Reference> getReferences() {
		return references;
	}

}
