package unittest;

import gate.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceContext;
import ru.ksu.niimm.cll.mocassin.nlp.impl.PosFeature;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceContextImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceProcessListener;
import ru.ksu.niimm.cll.mocassin.nlp.impl.WordFeature;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.thoughtworks.xstream.XStream;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class FeatureExtractorTest implements ReferenceProcessListener {
	@Inject
	private FeatureExtractor featureExtractor;

	@Test
	public void testGetReferenceContextList() throws Exception {
		getFeatureExtractor().addListener(this);
		getFeatureExtractor().processReferences();
	}

	@Override
	public void onReferenceFinish(Document document, List<Reference> references) {
		XStream xstream = new XStream();
		xstream.alias("reference", ReferenceImpl.class);

		ObjectOutputStream out;
		try {
			out = xstream
					.createObjectOutputStream(new FileWriter(String.format(
							"/tmp/refcontexts-data/%s.xml", document.getName())));
			for (Reference ref : references) {
				out.writeObject(ref);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public FeatureExtractor getFeatureExtractor() {
		return featureExtractor;
	}

}
