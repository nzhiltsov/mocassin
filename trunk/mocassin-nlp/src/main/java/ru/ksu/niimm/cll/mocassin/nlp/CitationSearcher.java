package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;


public interface CitationSearcher {
	List<String> getCitationSentences(String documentId);
}
