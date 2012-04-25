package ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance;

import java.util.Map;

import edu.uci.ics.jung.graph.Graph;

public interface ImportanceService<V, E> {
	Map<V, Float> computeImportanceRanks(Graph<V, E> graph);
}
