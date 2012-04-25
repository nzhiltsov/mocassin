package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class LatexDocumentDAOTest {
	@Inject
	private LatexDocumentDAO latexDocumentDAO;

	@Test
	public void testLoad() {
		LatexDocumentModel doc = latexDocumentDAO.load("ivm18");
		Assert.assertEquals(68, doc.getReferences().size());
	}
}
