package ru.ksu.niimm.cll.mocassin.nlp.latex;

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;

/**
 * Searcher that enriches representation (by extracting the text contents etc.)
 * of the underlying LaTeX document structure for searching purposes
 * 
 * @author nzhiltsov
 * 
 */
public interface LatexStructuralElementSearcher extends
		StructuralElementSearcher, ReferenceSearcher {
	void parse(InputStream stream, ParsedDocument parsedDocument, boolean closeStream)
			throws LatexSearcherParseException;
}
