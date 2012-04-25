package ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping.matchers;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping.Mapping;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Node;
import edu.uci.ics.jung.graph.Graph;

public interface Matcher {
	Mapping doMapping(Graph<Node, Edge> graph);
}
