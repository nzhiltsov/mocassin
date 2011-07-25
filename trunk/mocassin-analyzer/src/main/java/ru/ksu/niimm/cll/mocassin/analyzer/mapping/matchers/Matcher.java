package ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers;

import ru.ksu.niimm.cll.mocassin.analyzer.mapping.Mapping;
import ru.ksu.niimm.cll.mocassin.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.parser.latex.Node;
import edu.uci.ics.jung.graph.Graph;

public interface Matcher {
	Mapping doMapping(Graph<Node, Edge> graph);
}
