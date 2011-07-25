package ru.ksu.niimm.cll.mocassin.parser.latex;

import java.io.InputStream;


/**
 * Parser that analyzes a document and build the graph model for that
 * 
 * @author nzhiltsov
 * 
 */
public interface Parser {
	LatexDocumentModel parse(String docId, InputStream inputStream, boolean closeStream);
}
