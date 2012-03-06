package ru.ksu.niimm.cll.mocassin.analyzer;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

/**
 * Searcher for references in parsed documents
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceSearcher {
	Graph<StructuralElement, Reference> retrieveStructuralGraph(
			ParsedDocument document);
}
