package ru.ksu.niimm.cll.mocassin.parser.arxmliv;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class })
public class ArxmlivProducerTest {
	@Inject
	private ArxmlivProducer arxmlivProducer;

	@Test
	public void testProduce() {
		final String arxivId = "math/0002188";
		arxmlivProducer.produce(arxivId);
		Assert.assertTrue(new File("/opt/mocassin/arxmliv/"
				+ StringUtil.arxivid2filename(arxivId, "tex.xml")).exists());
	}
}
