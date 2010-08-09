package ru.ksu.niimm.cll.mocassin.parser.applications.importance;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

public interface ImportantNodeService {
	Map<Node, Float> computeImportanceRanks(Iterable<Edge<Node, Node>> graph);
}
