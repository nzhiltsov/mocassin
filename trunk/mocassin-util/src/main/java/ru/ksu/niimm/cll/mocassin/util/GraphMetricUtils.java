package ru.ksu.niimm.cll.mocassin.util;

import java.util.Collection;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import org.apache.commons.collections.CollectionUtils;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Transformer;

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
     * @param vertice
     *            given vertice
     * @param jumpProbability
     *            jump probability
     */
    public static <V, E> float computePageRank(Graph<V, E> graph, V vertice,
	    double jumpProbability) {
        Transformer<E, ? extends Number> transformer;
        if (graph instanceof DirectedSparseMultigraph) {
            final DirectedSparseMultigraph multigraph = (DirectedSparseMultigraph) graph;
            transformer = new Transformer<E, Number>() {
                @Override
                public Number transform(E e) {
                    int edgeSetSize = multigraph
                            .findEdgeSet(multigraph.getSource(e), multigraph.getDest(e)).size();
                    int outDegree = multigraph.outDegree(multigraph.getSource(e));
                    return (float) edgeSetSize / outDegree;
                }
            };
        } else {
            transformer = new Transformer<E, Number>() {
                @Override
                public Number transform(E e) {
                    return 1;
                }
            };
        }
        PageRank<V, E> pageRank =
                new PageRank<V, E>(graph, transformer, jumpProbability);
        pageRank.evaluate();
        return pageRank.getVertexScore(vertice).floatValue();
    }
}
