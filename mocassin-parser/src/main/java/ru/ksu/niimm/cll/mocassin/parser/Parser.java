package ru.ksu.niimm.cll.mocassin.parser;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

/**
 * Parser that analyzes a document and build the graph model for that
 * 
 * @author nzhiltsov
 * 
 */
public interface Parser {
	void load(InputStream inputStream) throws Exception;

	List<Edge<Node, Node>> getGraph();
}
