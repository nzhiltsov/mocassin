package ru.ksu.niimm.cll.mocassin.parser.mapping.matchers;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.mapping.Mapping;

public interface Matcher {
	Mapping doMapping(List<Edge<Node, Node>> graph);
}
