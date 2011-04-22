package ru.ksu.niimm.cll.mocassin.nlp;

import edu.uci.ics.jung.graph.Graph;

/**
 * Searcher for references in parsed documents
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceSearcher {
	Graph<StructuralElement, Reference> retrieveReferences(
			ParsedDocument document);
}
