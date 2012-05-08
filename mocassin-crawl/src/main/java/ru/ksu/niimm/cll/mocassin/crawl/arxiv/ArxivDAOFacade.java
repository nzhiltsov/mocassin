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
 * @author Nikita Zhiltsov
 * 
 */
public interface ArxivDAOFacade {
    /**
     * Returns metadata for a given id or null, if any error occurs
     * 
     * @param arxivId
     *            article id, e.g. 'math/0010215'
     * @returns article metadata
     */
    ArticleMetadata retrieve(String arxivId);

    /**
     * Roads the source of an article with given metadata
     * 
     * <p>
     * WARNING: <strong>calling method must close the returned input stream on
     * its own</strong>
     * 
     * @param metadata
     *            article metadata
     * @returns a stream thats contains the article contents in Latex
     */
    InputStream loadSource(ArticleMetadata metadata);

    /**
     * Loads PDF representation of an article with a given metadata
     * 
     * <p>
     * WARNING: <strong>calling method must close the returned input stream on
     * its own</strong>
     * 
     * @param metadata metadata
     * @returns a stream thats contains the article contents as PDF
     */
    InputStream loadPDF(ArticleMetadata metadata) throws LoadingPdfException;
}
