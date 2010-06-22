package ru.ksu.niimm.cll.mocassin.parser;

import java.io.Reader;
import java.util.List;

/**
 * Parser that analyzes a document and build the graph model for that
 * 
 * @author nzhiltsov
 * 
 */
public interface Parser {
	void load(Reader reader) throws Exception;

	List<Edge<Node, Node>> getGraph();
}
