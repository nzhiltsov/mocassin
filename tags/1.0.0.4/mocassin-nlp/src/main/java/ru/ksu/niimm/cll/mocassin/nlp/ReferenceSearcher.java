package ru.ksu.niimm.cll.mocassin.nlp;

import gate.Document;

import java.util.List;

/**
 * Searcher for references in arXMLiv formatted documents represented as GATE
 * documents
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceSearcher {
	List<Reference> retrieve(Document document);
}
