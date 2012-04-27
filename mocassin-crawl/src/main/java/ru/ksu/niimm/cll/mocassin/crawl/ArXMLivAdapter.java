package ru.ksu.niimm.cll.mocassin.crawl;

import java.util.List;

public interface ArXMLivAdapter {

	String handle(String arxivId) throws Exception;

	/**
	 * returns number of successfully processed papers
	 * 
	 * @param arxivIds
	 * @return
	 */
	int handle(List<String> arxivIds);

}