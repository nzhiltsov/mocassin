package ru.ksu.niimm.cll.mocassin.crawl.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Parser;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({LatexParserModule.class, PdfParserModule.class})
public class LatexParserTest {

	@Inject
	private Parser parser;

	private InputStream in;

	@Before
	public void init() throws FileNotFoundException {
		this.in = new FileInputStream("/opt/mocassin/tex/ivm18.tex");
	}

	@Test
	public void testParse() throws Exception {
		LatexDocumentModel model = getParser().parse("ivm18", getInputStream(), "cp866", true);
		Assert.assertTrue(model.getReferences().size() > 0);
	}

	public Parser getParser() {
		return parser;
	}

	public InputStream getInputStream() {
		return in;
	}

}
