package ru.ksu.niimm.cll.mocassin.parser.latex.builder;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import edu.uci.ics.jung.graph.Graph;

public interface StructureBuilder {

	Graph<Node, Edge> buildStructureGraph(LatexDocumentModel model);

}