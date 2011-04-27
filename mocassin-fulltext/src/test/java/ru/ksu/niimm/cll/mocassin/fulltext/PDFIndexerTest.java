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

	private InputStream in2;

	@Before
	public void init() {
		this.in = this.getClass().getResourceAsStream("/example.pdf");
		this.in2 = this.getClass().getResourceAsStream("/example2.pdf");
	}

	@Test
	public void testSaveAndGetPageNumber() throws Exception {
		try {
			this.pdfIndexer.save("http://localhost/example.pdf", in);

			int pageNumber = this.pdfIndexer.getPageNumber(
					"http://localhost/example.pdf", "\"Example 10.11\"");
			Assert.assertEquals(33, pageNumber);

			pageNumber = this.pdfIndexer.getPageNumber(
					"http://localhost/example.pdf", "\"Definition 7.4\"");
			Assert.assertEquals(22, pageNumber);

			pageNumber = this.pdfIndexer
					.getPageNumber("http://localhost/example.pdf",
							"\"3. FINITE GENERATION OF MULTI-SECTION RINGS ON MDS\" first prepare notation");
			Assert.assertEquals(10, pageNumber);
			// example2.pdf
			this.pdfIndexer.save("http://localhost/example2.pdf", in2);
			pageNumber = this.pdfIndexer.getPageNumber(
					"http://localhost/example2.pdf",
			"\"Corollary 3\" self-adjoint only where");
			Assert.assertEquals(3, pageNumber);
		} finally {
			this.in.close();
			this.in2.close();
		}

	}
}
