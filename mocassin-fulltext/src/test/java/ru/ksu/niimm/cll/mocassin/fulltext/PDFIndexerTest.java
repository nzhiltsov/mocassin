package ru.ksu.niimm.cll.mocassin.fulltext;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.lucene.index.IndexWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.providers.IndexWriterProvider;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { FullTextTestModule.class })
public class PDFIndexerTest {
	@Inject
	private PDFIndexer pdfIndexer;
	@Inject
	private IndexWriterProvider<IndexWriter> indexWriterProvider;

	private InputStream in;

	@Before
	public void init() {
		this.in = this.getClass().getResourceAsStream("/example.pdf");
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

			pageNumber = this.pdfIndexer.getPageNumber(
					"http://localhost/example.pdf",
					"\"3. FINITE GENERATION OF MULTI-SECTION RINGS ON MDS\"");
			Assert.assertEquals(10, pageNumber);
		} finally {
			indexWriterProvider.get().close();
		}

	}
}
