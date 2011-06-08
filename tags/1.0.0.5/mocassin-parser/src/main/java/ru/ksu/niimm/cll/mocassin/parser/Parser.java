package ru.ksu.niimm.cll.mocassin.parser;

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;

/**
 * Parser that analyzes a document and build the graph model for that
 * 
 * @author nzhiltsov
 * 
 */
public interface Parser {
	LatexDocumentModel parse(InputStream inputStream, boolean closeStream);
}
