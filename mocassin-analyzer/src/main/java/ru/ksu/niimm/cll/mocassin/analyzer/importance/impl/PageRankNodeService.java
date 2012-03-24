package ru.ksu.niimm.cll.mocassin.analyzer.importance.impl;

import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.parser.latex.Node;
import edu.uci.ics.jung.graph.Graph;

public class PageRankNodeService implements ImportantNodeService {

	private static final double PROBABILITY_OF_RANDOM_JUMP = 0.2;
	private final PageRankScorer<Node, Edge> pageRankScorer = new PageRankScorer<Node, Edge>();

	@Override
	public Map<Node, Float> computeImportanceRanks(Graph<Node, Edge> graph) {
		return pageRankScorer.computePageRanks(graph,
				PROBABILITY_OF_RANDOM_JUMP);
	}
}
