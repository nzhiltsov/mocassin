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
