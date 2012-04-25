package ru.ksu.niimm.cll.mocassin.search;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.search.PDFIndexer;

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
	
	private InputStream in3;

	@Before
	public void init() {
		this.in = this.getClass().getResourceAsStream("/example.pdf");
		this.in2 = this.getClass().getResourceAsStream("/example2.pdf");
		this.in3 = this.getClass().getResourceAsStream("/example3.pdf");
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
			
			this.pdfIndexer.save("http://arxiv.org/pdf/math/0205003", in3);
			pageNumber = this.pdfIndexer.getPageNumber(
					"http://arxiv.org/pdf/math/0205003",
			"\"Theorem 1.1\" Riemann hypothesis equivalent statement");
			Assert.assertEquals(2, pageNumber);
		} finally {
			this.in.close();
			this.in2.close();
			this.in3.close();
		}

	}
}
