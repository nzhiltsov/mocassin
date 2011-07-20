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
		 String arxivId = "math/0002188";
		String path = arxmlivProducer.produce(arxivId);
		Assert.assertEquals("/opt/mocassin/arxmliv/math_0002188.tex.xml", path);
		Assert.assertTrue(new File("/opt/mocassin/arxmliv/"
				+ StringUtil.arxivid2filename(arxivId, "tex.xml")).exists());
		arxivId = "math/0001036";
		path = arxmlivProducer.produce(arxivId);
		Assert.assertEquals("/opt/mocassin/arxmliv/math_0001036.tex.xml", path);
		Assert.assertTrue(new File("/opt/mocassin/arxmliv/"
				+ StringUtil.arxivid2filename(arxivId, "tex.xml")).exists());
	}
}
