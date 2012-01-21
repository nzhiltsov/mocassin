package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ PdfParserModule.class })
public class PdfHighlighterTest {
	@Inject
	private PdfHighlighter pdfHighlighter;

	@Test
	public void testGenerate() throws PdflatexCompilationException {
		pdfHighlighter.generateHighlightedPdf("ivm537", 1082, 240, 305);
		Assert.assertTrue(new File(String.format("/opt/mocassin/pdf/%s$%d.pdf",
				"ivm537", 1082)).exists());
	}

}
