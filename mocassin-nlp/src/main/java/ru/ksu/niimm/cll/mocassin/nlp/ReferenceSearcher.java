package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

/**
 * Searcher for references in parsed documents
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceSearcher {
	List<Reference> retrieveReferences(ParsedDocument document);
}
