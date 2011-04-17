package ru.ksu.niimm.cll.mocassin.parser.latex.builder;

import java.io.InputStream;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

public interface StructureBuilder {

	List<Edge<Node, Node>> buildStructureGraph(InputStream inputStream, boolean closeStream);

}