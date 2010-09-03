package unittest;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceContext;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class FeatureExtractorTest {
	@Inject
	private FeatureExtractor featureExtractor;

	@Test
	public void testGetReferenceContextList() throws Exception {
		List<ReferenceContext> list = getFeatureExtractor()
				.getReferenceContextList();
		print(list);
	}

	private void print(List<ReferenceContext> list) {
		for (ReferenceContext context : list) {
			System.out.println(context);
		}

	}

	public FeatureExtractor getFeatureExtractor() {
		return featureExtractor;
	}

}
