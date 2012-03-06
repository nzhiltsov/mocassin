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
	public void testGenerateSummaryAndGetPageNumber()
			throws GeneratePdfSummaryException {
		String arxivId = "ivm18";
		latex2pdfMapper.generateSummary(arxivId);
		Assert.assertTrue(new File("/opt/mocassin/pdfsync/"
				+ StringUtil.arxivid2filename(arxivId, "pdfsync")).exists());
		int pdfPageNumber = latex2pdfMapper.getPDFPageNumber(443, "ivm18");
		Assert.assertEquals(5, pdfPageNumber);
	}
}
