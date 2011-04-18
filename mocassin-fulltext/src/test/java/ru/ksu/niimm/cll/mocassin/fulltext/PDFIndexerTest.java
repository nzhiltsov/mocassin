package ru.ksu.niimm.cll.mocassin.fulltext;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { FullTextTestModule.class })
public class PDFIndexerTest {
	@Inject
	private PDFIndexer pdfIndexer;

	private InputStream in;

	@Before
	public void init() {
		this.in = this.getClass().getResourceAsStream("/example.pdf");
	}

	@Test
	public void testSaveAndGetPageNumber() throws PersistingDocumentException {
		this.pdfIndexer.save("http://localhost/example.pdf", in);
		int pageNumber = this.pdfIndexer
				.getPageNumber("http://localhost/example.pdf",
						"Let be normal projective varieties surjective morphism Mori dream space");
		Assert.assertEquals(2, pageNumber);

		pageNumber = this.pdfIndexer.getPageNumber(
				"http://localhost/example.pdf",
				"the quote the results on VGIT from the paper");
		Assert.assertEquals(20, pageNumber);
		pageNumber = this.pdfIndexer
				.getPageNumber(
						"http://localhost/example.pdf",
						"suppose that normal projective variety be effective visitors on such that are klt we consider the case when the cone spanned bt the divisors is a MDR");
		Assert.assertEquals(33, pageNumber);

	}
}
