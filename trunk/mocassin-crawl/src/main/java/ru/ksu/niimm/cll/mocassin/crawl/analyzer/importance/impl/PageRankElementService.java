package ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.impl;

import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.ImportantElementService;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;

public class PageRankElementService implements ImportantElementService {
	private static final double PROBABILITY_OF_RANDOM_JUMP = 0.2;
	private final PageRankScorer<StructuralElement, Reference> pageRankScorer = new PageRankScorer<StructuralElement, Reference>();

	@Override
	public Map<StructuralElement, Float> computeImportanceRanks(
			Graph<StructuralElement, Reference> graph) {
		return pageRankScorer.computePageRanks(graph,
				PROBABILITY_OF_RANDOM_JUMP);
	}

}
