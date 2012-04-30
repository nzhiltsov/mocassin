package ru.ksu.niimm.cll.mocassin.analyzer.importance;

import java.util.Map;

import ru.ksu.niimm.cll.mocassin.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.parser.latex.Node;
import edu.uci.ics.jung.graph.Graph;

public interface ImportantNodeService {
	Map<Node, Float> computeImportanceRanks(Graph<Node, Edge> graph);
}