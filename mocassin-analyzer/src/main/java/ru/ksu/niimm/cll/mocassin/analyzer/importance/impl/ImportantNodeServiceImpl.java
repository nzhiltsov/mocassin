package ru.ksu.niimm.cll.mocassin.analyzer.importance.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

import ru.ksu.niimm.cll.mocassin.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

public class ImportantNodeServiceImpl implements ImportantNodeService {

	@Override
	public Map<Node, Float> computeImportanceRanks(
			Iterable<Edge<Node, Node>> graph) {
		Hypergraph<Node, Edge<Node, Node>> hypergraph = new DirectedSparseMultigraph<Node, Edge<Node, Node>>();
		for (Edge<Node, Node> edge : graph) {
			Pair<Node> pair = new Pair<Node>(edge.getFrom(), edge.getTo());
			hypergraph.addEdge(edge, pair);
		}
		PageRank<Node, Edge<Node, Node>> pageRank = new PageRank<Node, Edge<Node, Node>>(
				hypergraph, 0.2);
		pageRank.evaluate();
		Collection<Node> vertices = hypergraph.getVertices();
		Map<Node, Float> node2score = new HashMap<Node, Float>();
		for (Node node : vertices) {
			Double score = pageRank.getVertexScore(node);
			Float roundScore = score.floatValue();
			node2score.put(node, roundScore);
		}
		return node2score;
	}
}
