package ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.analyzer.mapping.Mapping;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

public interface Matcher {
	Mapping doMapping(List<Edge<Node, Node>> graph);
}
