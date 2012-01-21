package ru.ksu.niimm.cll.mocassin.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.Parser;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

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
		this.in = new FileInputStream("/opt/mocassin/tex/math_0001036.tex");
	}

	@Test
	public void testParse() throws Exception {
		LatexDocumentModel model = getParser().parse("math/0001036", getInputStream(), true);
		Assert.assertTrue(model.getReferences().size() > 0);
	}

	public Parser getParser() {
		return parser;
	}

	public InputStream getInputStream() {
		return in;
	}

}