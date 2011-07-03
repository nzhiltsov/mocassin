package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;

public interface ArXMLivAdapter {

	void handle(String arxivId);
	/**
	 * returns number of successfully processed papers
	 * 
	 * @param arxivIds
	 * @return
	 */
	int handle(Set<String> arxivIds);

	List<ArxivArticleMetadata> loadArticles();

}