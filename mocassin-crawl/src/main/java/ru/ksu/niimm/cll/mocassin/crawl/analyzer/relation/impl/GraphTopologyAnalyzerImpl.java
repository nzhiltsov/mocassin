package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.GraphTopologyAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.RelationFeatureInfo;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;

public class GraphTopologyAnalyzerImpl implements GraphTopologyAnalyzer {

	@Override
	public List<RelationFeatureInfo> extractCandidateFeatures(
			Graph<StructuralElement, Reference> graph) {
		throw new UnsupportedOperationException("not implemented yet");
	}

}
