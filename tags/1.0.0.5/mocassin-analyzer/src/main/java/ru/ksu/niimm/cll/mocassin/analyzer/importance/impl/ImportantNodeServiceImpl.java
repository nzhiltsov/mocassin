package ru.ksu.niimm.cll.mocassin.analyzer.importance.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;

public class ImportantNodeServiceImpl implements ImportantNodeService {

	@Override
	public Map<Node, Float> computeImportanceRanks(Graph<Node, Edge> graph) {

		PageRank<Node, Edge> pageRank = new PageRank<Node, Edge>(graph, 0.2);
		pageRank.evaluate();
		Collection<Node> vertices = graph.getVertices();
		Map<Node, Float> node2score = new HashMap<Node, Float>();
		for (Node node : vertices) {
			Double score = pageRank.getVertexScore(node);
			Float roundScore = score.floatValue();
			node2score.put(node, roundScore);
		}
		return node2score;
	}
}
