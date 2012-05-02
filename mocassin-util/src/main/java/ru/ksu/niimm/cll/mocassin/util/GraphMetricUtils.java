package ru.ksu.niimm.cll.mocassin.util;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import edu.uci.ics.jung.graph.Graph;

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
	throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * this method computes PageRank of a given vertice by considering edge
     * weights and a given jump probability (alpha)
     * 
     * @param graph
     *            graph
     * @param vertice
     *            given vertice
     * @param jumpProbability
     *            jump probability
     */
    public static <V, E> float computePageRank(Graph<V, E> graph, V vertice,
	    double jumpProbability) {
	throw new UnsupportedOperationException("not implemented yet");
    }
}
