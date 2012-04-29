package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface GraphTopologyAnalyzer {
	/**
	 * returns candidate relations with features extracted
	 * 
	 * @param graph given structural graph
	 * @return
	 */
	List<RelationFeatureInfo> extractCandidateFeatures(
			Graph<StructuralElement, Reference> graph);
}
