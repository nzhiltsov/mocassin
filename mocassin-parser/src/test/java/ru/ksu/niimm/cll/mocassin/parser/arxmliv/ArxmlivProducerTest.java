package ru.ksu.niimm.cll.mocassin.parser.arxmliv;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.csvreader.CsvReader;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class ArxmlivProducerTest {
	@Inject
	private ArxmlivProducer arxmlivProducer;

	
	private final List<String> ids = new LinkedList<String>();

	@Before
	public void init() throws IOException {
		CsvReader reader = new CsvReader(
				new InputStreamReader(this.getClass().getClassLoader()
						.getResourceAsStream("mathnet_selected.csv")));
		reader.setTrimWhitespace(true);
		try {
			while (reader.readRecord()) {
				String id = reader.get(0);
				ids.add(id);
			}
		} finally {
			reader.close();
		}
	}

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

	@Test @Ignore
	public void testProduceForMathnetArticles() {
		for (String mathnetKey : ids) {
			try {
				arxmlivProducer.produce(mathnetKey);
			} catch (Exception e) { // nothing
			}
		}
	}
}
