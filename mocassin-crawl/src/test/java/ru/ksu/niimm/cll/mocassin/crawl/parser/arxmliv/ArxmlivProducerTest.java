package ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class ArxmlivProducerTest {
	@Inject
	private ArxmlivProducer arxmlivProducer;

	@Test
	public void testProduce() {
		String arxivId = "ivm18";
		String path = arxmlivProducer.produce(arxivId);
		Assert.assertEquals("/opt/mocassin/arxmliv/ivm18.tex.xml", path);
		Assert.assertTrue(new File("/opt/mocassin/arxmliv/"
				+ StringUtil.arxivid2filename(arxivId, "tex.xml")).exists());
		arxivId = "ivm537";
		path = arxmlivProducer.produce(arxivId);
		Assert.assertEquals("/opt/mocassin/arxmliv/ivm537.tex.xml", path);
		Assert.assertTrue(new File("/opt/mocassin/arxmliv/"
				+ StringUtil.arxivid2filename(arxivId, "tex.xml")).exists());
	}
}
