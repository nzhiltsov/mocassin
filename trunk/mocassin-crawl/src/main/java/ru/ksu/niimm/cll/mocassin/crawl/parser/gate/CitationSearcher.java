package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.util.LinkedList;
import java.util.List;


public interface CitationSearcher {
	List<String> getCitationSentences(String documentId);
	/**
	 * returns an ordered list of citations for the document with a given id
	 * 
	 * @return
	 */
	LinkedList<Citation> getCitations(String documentId);
}
