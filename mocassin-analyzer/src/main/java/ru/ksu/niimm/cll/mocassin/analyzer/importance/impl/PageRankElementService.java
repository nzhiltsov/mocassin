package ru.ksu.niimm.cll.mocassin.analyzer.importance.impl;

import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import ru.ksu.niimm.cll.mocassin.analyzer.importance.ImportantElementService;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;

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
