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

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;

/**
 * Facade that retrieves article metadata and loads the contents of an article
 * with a given arXiv identifier
 * 
 * @author nzhiltsov
 * 
 */
public interface ArxivDAOFacade {
	/**
	 * returns metadata or null if any error occurs
	 * 
	 * @param arxivId
	 * @return
	 */
	ArticleMetadata retrieve(String arxivId);

	/**
	 * loads the source of an article with given metadata
	 * 
	 * !!! WARNING calling method should close the returned input stream on its
	 * own
	 * 
	 * @param metadata
	 * @return
	 */
	InputStream loadSource(ArticleMetadata metadata);

	/**
	 * loads PDF representation of an article with given metadata
	 * 
	 * @param metadata
	 * @return
	 */
	InputStream loadPDF(ArticleMetadata metadata) throws LoadingPdfException;
}
