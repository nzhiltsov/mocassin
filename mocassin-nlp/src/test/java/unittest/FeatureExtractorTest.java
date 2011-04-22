package unittest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceProcessListener;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceXStream;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.thoughtworks.xstream.XStream;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
@Ignore("needs to fix the performance of the test")
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
						String.format(
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
	public void onReferenceFinish(ParsedDocument document,
			Graph<StructuralElement, Reference> graph) {
		XStream xstream = new ReferenceXStream();

		ObjectOutputStream out;
		try {
			out = xstream.createObjectOutputStream(new FileWriter(String
					.format("/tmp/refcontexts-data/%s.xml",
							document.getFilename())));
			for (Reference ref : graph.getEdges()) {
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
