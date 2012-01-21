package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ PdfParserModule.class })
public class Latex2PDFMapperTest {
	@Inject
	Latex2PDFMapper latex2pdfMapper;

	@Test
	public void testGetPdfPageNumber() {
		int pdfPageNumber = latex2pdfMapper.getPDFPageNumber(443,
				"ivm228");
		Assert.assertEquals(6, pdfPageNumber);
	}

	@Test
	public void testGenerateSummary() throws GeneratePdfSummaryException {
		String arxivId = "ivm228";
		latex2pdfMapper.generateSummary(arxivId);
		Assert.assertTrue(new File("/opt/mocassin/pdfsync/"
				+ StringUtil.arxivid2filename(arxivId, "pdfsync")).exists());
	}
}