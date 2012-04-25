package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

/**
 * Searcher that enriches representation (by extracting the text contents etc.)
 * of the underlying LaTeX document structure for searching purposes
 * 
 * @author nzhiltsov
 * 
 */
public interface LatexStructuralElementSearcher {
	Graph<StructuralElement, Reference> retrieveGraph(
			InputStream inputStream, ParsedDocument parsedDocument,
			boolean closeStream) throws LatexSearcherParseException;
}
