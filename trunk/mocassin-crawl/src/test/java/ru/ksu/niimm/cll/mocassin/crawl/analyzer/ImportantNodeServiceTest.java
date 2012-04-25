package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.util.Map;

import org.apache.commons.collections15.Predicate;
import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.EdgeType;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Node;

import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.graph.Graph;

public class ImportantNodeServiceTest extends AbstractRankingTest {

	@Test
	public void testComputeRanks() {
		for (Graph<Node, Edge> model : getModels()) {
			Map<Node, Float> node2score = computeNodeRanks(model);
			printScores(node2score);
		}

	}

	/**
	 * compute node ranks and return node-to-value map sorted by descending
	 * values
	 * 
	 * @param documentModel
	 * @return
	 */
	protected Map<Node, Float> computeNodeRanks(Graph<Node, Edge> edges) {
		Predicate<Edge> predicate = new Predicate<Edge>() {

			@Override
			public boolean evaluate(Edge edge) {
				if (edge.getContext().getEdgeType() == EdgeType.REFERS_TO) {
					return true;
				}
				return false;
			}

		};
		EdgePredicateFilter<Node, Edge> refersToFilter = new EdgePredicateFilter<Node, Edge>(
				predicate);
		Graph<Node, Edge> refersToSubgraph = refersToFilter.transform(edges);

		Map<Node, Float> node2score = getImportantNodeService()
				.computeImportanceRanks(refersToSubgraph);
		Map<Node, Float> sortedMap = sortByValue(node2score);
		return sortedMap;
	}

}
