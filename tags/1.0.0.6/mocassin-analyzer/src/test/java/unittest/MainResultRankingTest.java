package unittest;

import java.util.Map;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.parser.latex.Node;
import unittest.info.RankedNodeInfo;
import edu.uci.ics.jung.graph.Graph;

public class MainResultRankingTest extends ImportantNodeServiceTest {
	@Test
	public void testRankMainResult() {
		for (Graph<Node, Edge> model : getModels()) {
			Map<Node, Float> node2score = computeNodeRanks(model);
			int i = 0;
			for (Node node : node2score.keySet()) {
				i++;
				if (node.getLabelText() != null
						&& node.getLabelText().toLowerCase().contains("main")) {
					RankedNodeInfo info = fillRankedInfo(node2score, i, node,
							model);
					print(info);
					break;
				}
			}
		}

	}

	private void print(RankedNodeInfo info) {
		System.out.println(info);
	}

	private RankedNodeInfo fillRankedInfo(Map<Node, Float> node2score, int i,
			Node node, Graph<Node, Edge> model) {
		RankedNodeInfo info = new RankedNodeInfo();
		info.setElementId(node.toString());
		info.setLabelText(node.getLabelText());
		info.setRankNumber(i);
		info.setRankValue(node2score.get(node));
		info.setElementsCount(node2score.size());
		return info;
	}
}
