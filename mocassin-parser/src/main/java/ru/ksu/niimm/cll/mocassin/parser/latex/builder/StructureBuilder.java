package ru.ksu.niimm.cll.mocassin.parser.latex.builder;

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import edu.uci.ics.jung.graph.Graph;

public interface StructureBuilder {

	Graph<Node, Edge> buildStructureGraph(InputStream inputStream,
			boolean closeStream);

}