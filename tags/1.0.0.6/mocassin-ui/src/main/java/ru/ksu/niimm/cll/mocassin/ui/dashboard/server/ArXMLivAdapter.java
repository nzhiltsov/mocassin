package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;

public interface ArXMLivAdapter {

	void handle(String arxivId) throws Exception;
	/**
	 * returns number of successfully processed papers
	 * 
	 * @param arxivIds
	 * @return
	 */
	int handle(List<String> arxivIds);

	List<ArxivArticleMetadata> loadArticles();

}