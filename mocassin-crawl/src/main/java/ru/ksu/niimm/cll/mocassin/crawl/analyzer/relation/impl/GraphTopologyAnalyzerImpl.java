/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.GraphTopologyAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.RelationFeatureInfo;
import edu.uci.ics.jung.graph.Graph;

import static ru.ksu.niimm.cll.mocassin.util.GraphMetricUtils.*;

public class GraphTopologyAnalyzerImpl implements GraphTopologyAnalyzer {
    /**
     * Exponential proportion parameter using for computing Katz coefficient
     * values
     */
    private static final double KATZ_EXP_PROPORTION = 0.005;
    /**
     * Fixed jump probability used while computing PageRank scores for elements
     * in the document graph
     */
    protected static final double JUMP_PROBABILITY = .2;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RelationFeatureInfo> extractCandidateFeatures(
	    Graph<StructuralElement, Reference> graph) {
	ArrayList<RelationFeatureInfo> result = new ArrayList<RelationFeatureInfo>();

	ArrayList<StructuralElement> elements = new ArrayList<StructuralElement>(
		graph.getVertices());

	Map<StructuralElement, Float> element2PR = computePageRank(graph,
		elements, JUMP_PROBABILITY);

	int size = elements.size();

	for (int i = 0; i < size - 1; i++) {
	    StructuralElement from = elements.get(i);
	    Float fromPR = element2PR.get(from);

	    ArrayList<StructuralElement> iNeighbors = new ArrayList<StructuralElement>(
		    graph.getNeighbors(from));

	    for (int j = i + 1; j < size; j++) {
		StructuralElement to = elements.get(j);
		ArrayList<StructuralElement> jNeighbors = new ArrayList<StructuralElement>(
			graph.getNeighbors(to));

		float jaccard = computeNeighborJaccard(iNeighbors, jNeighbors);

		int preferentialAttachmentScore = computePreferentialAttachmentScore(
			iNeighbors, jNeighbors);

		Float toPR = element2PR.get(to);

		float katzCoefficient = (float) computeKatzCoefficient(graph,
			from, to, KATZ_EXP_PROPORTION);

		RelationFeatureInfo info = new RelationFeatureInfo.Builder(
			from, to)
			.neigborJaccard(jaccard)
			.preferentialAttachmentScore(
				preferentialAttachmentScore).fromPR(fromPR)
			.toPR(toPR).katzCoefficient(katzCoefficient).build();
		result.add(info);
	    }
	}
	return result;
    }
}
