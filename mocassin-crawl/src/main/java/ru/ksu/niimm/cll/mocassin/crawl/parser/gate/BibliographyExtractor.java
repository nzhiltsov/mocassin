package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.util.Map;

public interface BibliographyExtractor {
	/**
	 * returns the number of a given bibliographic reference
	 * 
	 * @return
	 */
	Integer getNumber(String fromKey, String toKey);
	
	String getToKey(String fromKey, int number);
}
