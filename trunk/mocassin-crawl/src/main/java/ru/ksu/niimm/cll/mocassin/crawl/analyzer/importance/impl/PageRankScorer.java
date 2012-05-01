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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;

public class PageRankScorer<V, E> {
	/*
	 * compute Page Rank for vertices
	 */
	public Map<V, Float> computePageRanks(Graph<V, E> graph,
			double alphaParameter) {
		PageRank<V, E> pageRank = new PageRank<V, E>(graph, alphaParameter);
		pageRank.evaluate();
		Collection<V> vertices = graph.getVertices();
		Map<V, Float> node2score = new HashMap<V, Float>();
		for (V node : vertices) {
			Double score = pageRank.getVertexScore(node);
			Float roundScore = score.floatValue();
			node2score.put(node, roundScore);
		}
		return node2score;
	}
}
