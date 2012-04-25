package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
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
