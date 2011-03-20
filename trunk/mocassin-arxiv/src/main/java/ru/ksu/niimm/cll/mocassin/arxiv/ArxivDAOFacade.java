package ru.ksu.niimm.cll.mocassin.arxiv;

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
}
