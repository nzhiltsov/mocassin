package ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.texlipse.model.OutlineNode;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeContextImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.Analyzer;

/**
 * Analyzer that builds the node containment tree
 * 
 * @author nzhiltsov
 * 
 */
public class ContainmentAnalyzer implements Analyzer {

	@Override
	public List<Edge<Node, Node>> analyze(LatexDocumentModel model) {
		List<OutlineNode> tree = model.getTree();
		List<Edge<Node, Node>> edges = new ArrayList<Edge<Node, Node>>();

		LinkedList<OutlineNode> queue = new LinkedList<OutlineNode>();
		queue.addAll(tree);
		while (!queue.isEmpty()) {
			OutlineNode node = queue.pop();

			ArrayList<OutlineNode> children = node.getChildren();

			if (children != null) {
				String nodeId = String.format("%d:%d", node.getBeginLine(),
						node.getOffsetOnLine());
				Node from = new NodeImpl(nodeId, node.getName());
				for (OutlineNode child : children) {
					Edge<Node, Node> edge = new EdgeImpl();
					String childId = String.format("%d:%d", child
							.getBeginLine(), child.getOffsetOnLine());
					Node to = new NodeImpl(childId, child.getName());
					EdgeContext context = new EdgeContextImpl(EdgeType.CONTAINS);
					edge.connect(from, to, context);
					edges.add(edge);
					queue.add(child);
				}
			}
		}
		return edges;
	}

}
