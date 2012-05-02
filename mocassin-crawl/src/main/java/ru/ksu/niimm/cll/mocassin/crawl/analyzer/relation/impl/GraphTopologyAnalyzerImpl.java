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

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections.CollectionUtils;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.GraphTopologyAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.RelationFeatureInfo;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;

public class GraphTopologyAnalyzerImpl implements GraphTopologyAnalyzer {

	@Override
	public List<RelationFeatureInfo> extractCandidateFeatures(
			Graph<StructuralElement, Reference> graph) {
        ArrayList<RelationFeatureInfo> result = new ArrayList<RelationFeatureInfo>();

        ArrayList<StructuralElement> elements = new ArrayList<StructuralElement>(graph.getVertices());
        int size = graph.getVertices().size();
        for (int i = 0; i < size - 1; i++) {
            ArrayList<StructuralElement> iNeighbors =
                    new ArrayList<StructuralElement>(graph.getNeighbors(elements.get(i)));
            for (int j = i + 1; j < size; j++) {
                ArrayList<StructuralElement> jNeighbors =
                        new ArrayList<StructuralElement>(graph.getNeighbors(elements.get(j)));

                int intersection = CollectionUtils.intersection(iNeighbors, jNeighbors).size();
                int union = CollectionUtils.union(iNeighbors, jNeighbors).size();

                float jaccard = (float) intersection / union;

                RelationFeatureInfo info = new RelationFeatureInfo(elements.get(i), elements.get(j), jaccard);
                result.add(info);
            }
        }
		return result;
	}

}
