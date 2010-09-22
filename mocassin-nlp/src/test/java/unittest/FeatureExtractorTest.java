package unittest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceContext;
import ru.ksu.niimm.cll.mocassin.nlp.impl.PosFeature;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceContextImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.WordFeature;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.thoughtworks.xstream.XStream;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class FeatureExtractorTest {
	@Inject
	private FeatureExtractor featureExtractor;

	@Test
	public void testGetReferenceContextList() throws Exception {
		Map<String, List<Reference>> doc2refs = getFeatureExtractor()
				.getReferencesPerDocument();
		save(doc2refs);
	}

	private void save(Map<String, List<Reference>> doc2refs) throws IOException {
		/*XStream xstream = new XStream();
		xstream.alias("context", ReferenceContextImpl.class);
		xstream.alias("word", WordFeature.class);
		xstream.alias("pos", PosFeature.class);
		ObjectOutputStream out = xstream
				.createObjectOutputStream(new FileWriter(
						"/tmp/refcontexts-data.xml"));
		for (ReferenceContext context : doc2refs) {
			out.writeObject(context);
		}
		out.close();*/
	}

	public FeatureExtractor getFeatureExtractor() {
		return featureExtractor;
	}

}
