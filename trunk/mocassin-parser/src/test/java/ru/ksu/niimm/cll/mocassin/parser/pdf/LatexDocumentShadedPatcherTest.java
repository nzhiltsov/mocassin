package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.LatexDocumentShadedPatcher;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class LatexDocumentShadedPatcherTest {
	@Inject
	private LatexDocumentShadedPatcher latexDocumentShadedPatcher;

	@Test
	public void testPatch() {
		String arxivId = "math/0001036";
		latexDocumentShadedPatcher.patch(arxivId, 181, 184, 1009);
		Assert.assertTrue(new File("/opt/mocassin/shaded-tex/"
				+ StringUtil.arxivid2gateid(arxivId) + "$1009.tex").exists());
	}
}
