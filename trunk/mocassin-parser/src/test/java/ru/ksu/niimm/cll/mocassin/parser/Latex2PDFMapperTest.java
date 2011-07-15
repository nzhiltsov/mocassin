package ru.ksu.niimm.cll.mocassin.parser;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class })
public class Latex2PDFMapperTest {
	@Inject
	Latex2PDFMapper latex2pdfMapper;

	@Test
	public void testGetPdfPageNumber() {
		int pdfPageNumber = latex2pdfMapper.getPDFPageNumber(558, "math/0001008");
		Assert.assertEquals(5, pdfPageNumber);
	}
}
