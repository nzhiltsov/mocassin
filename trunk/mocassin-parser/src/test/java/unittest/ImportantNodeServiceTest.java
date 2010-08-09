package unittest;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class ImportantNodeServiceTest extends AbstractRankingTest {

	@Test
	public void testComputeRanks() {
		for (LatexDocumentModel model : getModels()) {
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
	protected Map<Node, Float> computeNodeRanks(LatexDocumentModel documentModel) {
		List<Edge<Node, Node>> edges = getStructureAnalyzer().analyze(
				documentModel);
		Predicate<Edge<Node, Node>> predicate = new Predicate<Edge<Node, Node>>() {

			@Override
			public boolean apply(Edge<Node, Node> edge) {
				if (edge.getContext().getEdgeType() == EdgeType.REFERS_TO) {
					return true;
				}
				return false;
			}
		};
		Iterable<Edge<Node, Node>> hypergraph = Iterables.filter(edges,
				predicate);
		Map<Node, Float> node2score = getImportantNodeService()
				.computeImportanceRanks(hypergraph);
		Map<Node, Float> sortedMap = sortByValue(node2score);
		return sortedMap;
	}

}
