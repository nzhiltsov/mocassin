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
package ru.ksu.niimm.cll.mocassin.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import edu.uci.ics.jung.algorithms.matrix.GraphMatrixOperations;
import edu.uci.ics.jung.algorithms.util.Indexer;
import org.apache.commons.collections.CollectionUtils;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.BidiMap;

public class GraphMetricUtils {
    private GraphMetricUtils() {
    }

    /**
     * this method computes Jaccard coefficient for neighbors of a given pair of
     * structural elements
     */
    public static float computeNeighborJaccard(Collection<?> iNeighbors,
	    Collection<?> jNeighbors) {
	int intersection = CollectionUtils.intersection(iNeighbors, jNeighbors)
		.size();
	int union = CollectionUtils.union(iNeighbors, jNeighbors).size();

	float jaccard = (float) intersection / union;
	return jaccard;
    }

    /**
     * this method computes the preferential attachment score as a product of
     * the both elements' neighborhood cardinalities
     */
    public static int computePreferentialAttachmentScore(
	    Collection<?> iNeighbors, Collection<?> jNeighbors) {
	return iNeighbors.size() * jNeighbors.size();
    }

    /**
     * this method computes PageRank of a given vertice by considering edge
     * weights and a given jump probability (alpha)
     * 
     * @param graph
     *            graph
     * @param jumpProbability
     *            jump probability
     */
    public static <V, E> Map<V, Float> computePageRank(final Graph<V, E> graph,
	    Collection<V> collection, double jumpProbability) {
	PageRank<V, E> pageRank = new PageRank<V, E>(graph, jumpProbability);
	pageRank.evaluate();
	Map<V, Float> element2PR = new HashMap<V, Float>();
	for (V vertice : collection) {
	    float pr = pageRank.getVertexScore(vertice).floatValue();
	    element2PR.put(vertice, pr);
	}
	return element2PR;
    }

    public static <V, E> double computeKatzCoefficient(Graph<V, E> graph, V from, V to, double exponentialProportion) {
        DoubleMatrix2D matrix = GraphMatrixOperations.graphToSparseMatrix(graph);

        Algebra algebra = new Algebra();
        DoubleMatrix2D identity = algebra.pow(matrix, 0);
        DoubleMatrix2D identity2 = (DoubleMatrix2D) identity.clone();

        matrix = matrix.zMult(identity, null, exponentialProportion, 0, false, false);

        matrix = algebra.inverse(identity.assign(matrix, Functions.minus)).assign(identity2, Functions.minus);

        BidiMap<V, Integer> indexer = Indexer.<V>create(graph.getVertices());

        int i = indexer.get(from);
        int j = indexer.get(to);

        return matrix.get(i,j);
    }
}
