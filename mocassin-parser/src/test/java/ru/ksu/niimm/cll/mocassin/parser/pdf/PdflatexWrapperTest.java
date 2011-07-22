package ru.ksu.niimm.cll.mocassin.parser.pdf;

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
public class PdflatexWrapperTest {
	@Inject
	private PdflatexWrapper pdflatexWrapper;

	@Test
	public void testCompilePatched() throws PdflatexCompilationException {
		String arxivId = "math/0002188";
		pdflatexWrapper.compilePatched(arxivId);

		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdf")).exists());
		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdfsync")).exists());
		arxivId = "math/0001036";
		pdflatexWrapper.compilePatched(arxivId);

		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdf")).exists());
		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdfsync")).exists());
	}

	@Test
	public void testCompileShaded() throws PdflatexCompilationException {
		String arxivId = "math/0001036";
		pdflatexWrapper.compileShaded(arxivId, 1009);
		Assert.assertTrue(new File(String.format("/opt/mocassin/pdf/%s$%d.pdf",
				StringUtil.arxivid2gateid(arxivId), 1009)).exists());
	}
}
