/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.arxiv;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;

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
