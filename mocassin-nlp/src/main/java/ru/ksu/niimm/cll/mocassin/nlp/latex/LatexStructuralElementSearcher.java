package ru.ksu.niimm.cll.mocassin.nlp.latex;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.sourceforge.texlipse.texparser.lexer.LexerException;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;

public interface LatexStructuralElementSearcher extends
		StructuralElementSearcher, ReferenceSearcher {
	void parse(InputStream stream, ParsedDocument parsedDocument)
			throws LatexSearcherParseException;
}
