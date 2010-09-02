package unittest;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class FeatureExtractorTest {
	@Inject
	private FeatureExtractor featureExtractor;

	@Test
	public void testGetReference2FeatureMap() throws Exception {
		getFeatureExtractor().getReference2FeatureMap();
	}

	public FeatureExtractor getFeatureExtractor() {
		return featureExtractor;
	}

}
