package ru.ksu.niimm.cll.mocassin.parser.latex;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import net.sourceforge.texlipse.model.OutlineNode;
import net.sourceforge.texlipse.texparser.lexer.LexerException;

public interface TreeParser {

	LatexDocumentModel parseTree(Reader reader) throws LexerException,
			IOException;

}