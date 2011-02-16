package unittest;

import gate.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceProcessListener;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceXStream;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.thoughtworks.xstream.XStream;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class FeatureExtractorTest implements ReferenceProcessListener {
	private static final String REF_CONTEXT_DATA_OUTPUT_DIR = "/tmp/refcontexts-data";
	@Inject
	private FeatureExtractor featureExtractor;

	@Before
	public void init() {
		File dir = new File(REF_CONTEXT_DATA_OUTPUT_DIR);
		if (!dir.canRead()) {
			if (!dir.mkdir()) {
				throw new RuntimeException(
						String
								.format(
										"couldn't create root folder to save refcontext data with following path: %s",
										REF_CONTEXT_DATA_OUTPUT_DIR));
			}
		}
	}

	@Test
	public void testGetReferenceContextList() throws Exception {
		getFeatureExtractor().addListener(this);
		getFeatureExtractor().processReferences(0);
	}

	@Override
	public void onReferenceFinish(Document document, List<Reference> references) {
		XStream xstream = new ReferenceXStream();

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
