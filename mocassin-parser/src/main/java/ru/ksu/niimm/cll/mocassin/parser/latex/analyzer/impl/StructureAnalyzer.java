package ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.OutlineNode;
import net.sourceforge.texlipse.model.ReferenceEntry;
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
 * Analyzer that builds graph with labels/references and containment relations
 * as its edges
 * 
 * @author nzhiltsov
 * 
 */
public class StructureAnalyzer implements Analyzer {
	private LatexDocumentModel model;

	@Override
	public List<Edge<Node, Node>> analyze(LatexDocumentModel model) {
		prepareAnalysis(model);

		List<Edge<Node, Node>> edges = new ArrayList<Edge<Node, Node>>();

		Stack<OutlineNode> stack = new Stack<OutlineNode>();
		List<OutlineNode> tree = getModel().getTree();
		for (int i = tree.size() - 1; i >= 0; i--) {
			stack.push(tree.get(i));
		}
		while (!stack.isEmpty()) {
			OutlineNode node = stack.pop();

			ArrayList<OutlineNode> children = node.getChildren();

			if (children != null) {
				String nodeId = String.format("%d:%d", node.getBeginLine(),
						node.getOffsetOnLine());
				Node from = new NodeImpl(nodeId, node.getName());
				for (OutlineNode child : children) {
					if (child.getType() == OutlineNode.TYPE_LABEL) {
						List<DocumentReference> references = getReferencesForLabel(child);
						List<Edge<Node, Node>> referenceEdges = getReferenceEdges(
								references, from);
						edges.addAll(referenceEdges);
					} else {
						Edge<Node, Node> edge = makeEdge(from, child,
								EdgeType.CONTAINS);
						edges.add(edge);
						stack.add(child);
					}

				}
			}
		}
		return edges;
	}

	private Edge<Node, Node> makeEdge(Node from, OutlineNode toNode,
			EdgeType edgeType) {
		Edge<Node, Node> edge = new EdgeImpl();
		String childId = String.format("%d:%d", toNode.getBeginLine(), toNode
				.getOffsetOnLine());
		Node to = new NodeImpl(childId, toNode.getName());
		EdgeContext context = new EdgeContextImpl(edgeType);
		edge.connect(from, to, context);
		return edge;
	}

	private Edge<Node, Node> makeInverseEdge(OutlineNode fromNode, Node to,
			EdgeType edgeType) {
		Edge<Node, Node> edge = new EdgeImpl();
		String childId = String.format("%d:%d", fromNode.getBeginLine(),
				fromNode.getOffsetOnLine());
		Node from = new NodeImpl(childId, fromNode.getName());
		EdgeContext context = new EdgeContextImpl(edgeType);
		edge.connect(from, to, context);
		return edge;
	}

	private List<Edge<Node, Node>> getReferenceEdges(
			List<DocumentReference> references, Node to) {
		List<Edge<Node, Node>> edges = new ArrayList<Edge<Node, Node>>();
		for (DocumentReference reference : references) {
			OutlineNode parent = getReferenceParent(reference);
			Edge<Node, Node> edge = makeInverseEdge(parent, to,
					EdgeType.REFERS_TO);
			edges.add(edge);
		}
		return edges;
	}

	/**
	 * depth-first traversal using 'beginLine' and 'endLine' info to follow the
	 * path
	 * 
	 * @param reference
	 * @return
	 */
	private OutlineNode getReferenceParent(DocumentReference reference) {
		Stack<OutlineNode> stack = new Stack<OutlineNode>();
		for (OutlineNode root : getModel().getTree()) {
			if (reference.startLine >= root.getBeginLine()
					&& reference.startLine <= root.getEndLine()) {
				stack.push(root);
				break;
			}
		}
		while (!stack.isEmpty()) {
			OutlineNode node = stack.pop();
			ArrayList<OutlineNode> children = node.getChildren();
			if (children == null) {
				return node;
			} else {
				boolean foundChild = false;
				for (OutlineNode child : children) {
					if (reference.startLine >= child.getBeginLine()
							&& reference.startLine <= child.getEndLine()) {
						stack.push(child);
						foundChild = true;
						break;
					}
				}
				if (!foundChild) {
					return node;
				}
			}

		}
		throw new IllegalStateException(
				String
						.format(
								"parent of the following reference couldn't be found: [key: %s, startLine: %d]",
								reference.key, reference.startLine));
	}

	private void prepareAnalysis(LatexDocumentModel model) {
		setModel(model);
	}

	private List<DocumentReference> getReferencesForLabel(OutlineNode child) {

		ReferenceEntry label = getLabel(child);
		List<DocumentReference> refs = new ArrayList<DocumentReference>();
		Iterator<DocumentReference> iterator = getReferences().iterator();
		while (iterator.hasNext()) {
			DocumentReference ref = iterator.next();
			if (ref.getKey().equals(label.key)) {
				refs.add(ref);
			} else if (!refs.isEmpty()) {
				break; // 'cause references list is ordered by 'key'
			}
		}

		return refs;
	}

	private ReferenceEntry getLabel(OutlineNode child) {
		ReferenceEntry foundLabel = null;
		int i = 0;
		while (i <= getLabels().size() - 1) {
			ReferenceEntry label = getLabels().get(i);
			boolean found = label.key.equals(child.getName())
					&& label.startLine == child.getBeginLine();
			if (found) {
				foundLabel = label;
			}
			i++;
		}
		return foundLabel;
	}

	private void setModel(LatexDocumentModel model) {
		this.model = model;
	}

	private LatexDocumentModel getModel() {
		return model;
	}

	private List<DocumentReference> getReferences() {
		return getModel().getReferences();
	}

	private List<ReferenceEntry> getLabels() {
		return getModel().getLabels();
	}

}
