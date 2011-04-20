package ru.ksu.niimm.cll.mocassin.arxiv;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { ArxivModule.class })
public class LoadPDFTest {
	@Inject
	ArxivDAOFacade arxivDAOFacade;

	private ArticleMetadata metadata;

	@Before
	public void init() {
		metadata = this.arxivDAOFacade.retrieve("math/0205003");
	}

	@Test
	public void testLoadPdf() throws IOException, LoadingPdfException {
		InputStream inputStream = this.arxivDAOFacade.loadPDF(metadata);
		Assert.assertNotNull(inputStream);
		inputStream.close();
	}
}
