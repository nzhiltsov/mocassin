package ru.ksu.niimm.cll.mocassin.nlp.latex;

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;

public interface LatexStructuralElementSearcher extends
		StructuralElementSearcher, ReferenceSearcher {
	void parse(InputStream stream, ParsedDocument parsedDocument)
			throws LatexSearcherParseException;
}
