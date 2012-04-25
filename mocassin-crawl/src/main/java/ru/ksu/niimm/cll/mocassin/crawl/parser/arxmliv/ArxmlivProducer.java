package ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv;

public interface ArxmlivProducer {
	/**
	 * produces the <a href="https://trac.kwarc.info/arXMLiv">arXMLiv</a>
	 * representation of a paper with a given arXiv identifier
	 * 
	 * @param arxivId
	 * @return local file path
	 */
	String produce(String arxivId);
}
