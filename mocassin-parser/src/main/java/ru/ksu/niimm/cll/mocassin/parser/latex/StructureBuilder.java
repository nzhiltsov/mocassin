package ru.ksu.niimm.cll.mocassin.parser.latex;

import edu.uci.ics.jung.graph.Graph;

public interface StructureBuilder {

	Graph<Node, Edge> buildStructureGraph(LatexDocumentModel model);

}