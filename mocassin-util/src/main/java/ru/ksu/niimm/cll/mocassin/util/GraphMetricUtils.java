package ru.ksu.niimm.cll.mocassin.util;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

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
}
