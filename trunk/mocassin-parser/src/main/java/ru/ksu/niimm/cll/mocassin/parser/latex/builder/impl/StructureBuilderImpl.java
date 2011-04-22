package ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.OutlineNode;
import net.sourceforge.texlipse.model.ReferenceEntry;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeContextImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.NewtheoremCommand;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Analyzer that builds graph with labels/references and containment relations
 * as its edges
 * 
 * @author nzhiltsov
 * 
 */
public class StructureBuilderImpl implements StructureBuilder {
	@Inject
	private Logger logger;
	@Inject
	private Parser parser;

	private static final String NODE_ID_FORMAT = "%d_%d";
	private LatexDocumentModel model;
	private Graph<Node, Edge> hypergraph;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.Builder#analyze(ru
	 * .ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel)
	 */
	@Override
	public synchronized Graph<Node, Edge> buildStructureGraph(
			InputStream inputStream, boolean closeStream) {
		/**
		 * TODO: this method is declared synchronized; this leads to lower
		 * performance; such a solution should be revised by considering the
		 * overall application architecture
		 **/

		this.hypergraph = new DirectedSparseMultigraph<Node, Edge>();
		LatexDocumentModel parsedModel = this.parser.parse(inputStream,
				closeStream);
		if (parsedModel == null) {
			logger.log(Level.SEVERE,
					"The parsed model is null. An empty graph will be returned");
			return hypergraph;
		}
		setModel(parsedModel);

		Stack<OutlineNode> stack = new Stack<OutlineNode>();
		List<OutlineNode> tree = getModel().getTree();
		OutlineNode documentRoot = getModel().getDocumentRoot();
		for (int i = tree.size() - 1; i >= 0; i--) {
			OutlineNode treeItem = tree.get(i);
			stack.push(treeItem);

			Node documentRootNode = new NodeImpl(
					String.format(NODE_ID_FORMAT, documentRoot.getBeginLine(),
							documentRoot.getOffsetOnLine()),
					documentRoot.getName());
			documentRootNode.setBeginLine(documentRoot.getBeginLine());
			documentRootNode.setEndLine(documentRoot.getEndLine());
			documentRootNode.setOffset(documentRoot.getOffsetOnLine());
			documentRootNode.setEnvironment(false);
			addEdge(documentRootNode, treeItem, EdgeType.CONTAINS);
		}
		while (!stack.isEmpty()) {
			OutlineNode node = stack.pop();

			ArrayList<OutlineNode> children = node.getChildren();

			if (children != null) {
				String nodeId = String.format(NODE_ID_FORMAT,
						node.getBeginLine(), node.getOffsetOnLine());
				Node from = new NodeImpl(nodeId, extractName(node));
				from.setBeginLine(node.getBeginLine());
				from.setEndLine(node.getEndLine());
				from.setOffset(node.getOffsetOnLine());
				from.setEnvironment(node.getType() == OutlineNode.TYPE_ENVIRONMENT);
				for (OutlineNode child : children) {
					if (child.getType() == OutlineNode.TYPE_LABEL) {
						from.setLabelText(child.getName());
						List<DocumentReference> references = getReferencesForLabel(child);
						addReferenceEdges(references, from);
					} else {
						addEdge(from, child, EdgeType.CONTAINS);
						stack.add(child);
					}

				}
			}
		}
		return this.hypergraph;
	}

	private void addEdge(Node from, OutlineNode toNode, EdgeType edgeType) {
		Edge edge = new EdgeImpl();
		String childId = String.format(NODE_ID_FORMAT, toNode.getBeginLine(),
				toNode.getOffsetOnLine());
		String nodeName = extractName(toNode);
		Node to = new NodeImpl(childId, nodeName);
		to.setBeginLine(toNode.getBeginLine());
		to.setEndLine(toNode.getEndLine());
		to.setOffset(toNode.getOffsetOnLine());
		to.setEnvironment(toNode.getType() == OutlineNode.TYPE_ENVIRONMENT);
		String labelText = getLabelText(toNode);
		to.setLabelText(labelText);
		EdgeContext context = new EdgeContextImpl(edgeType);
		edge.setContext(context);
		addEdge(edge, from, to);
	}

	private String extractName(OutlineNode node) {
		String nodeName;
		if (node.getType() == OutlineNode.TYPE_SECTION
				|| node.getType() == OutlineNode.TYPE_SUBSECTION
				|| node.getType() == OutlineNode.TYPE_SUBSUBSECTION) {
			nodeName = "section";
		} else {
			nodeName = node.getName();
			NewtheoremCommand foundCommand = Iterables.find(getModel()
					.getNewtheorems(), new NewtheoremCommand.KeyPredicate(
					nodeName), null);
			if (foundCommand != null) {
				nodeName = foundCommand.getTitle();
			}
		}
		return nodeName;
	}

	private void addInverseEdge(OutlineNode fromNode, Node to, EdgeType edgeType) {
		Edge edge = new EdgeImpl();
		String childId = String.format(NODE_ID_FORMAT, fromNode.getBeginLine(),
				fromNode.getOffsetOnLine());
		String nodeName = extractName(fromNode);
		Node from = new NodeImpl(childId, nodeName);
		from.setBeginLine(fromNode.getBeginLine());
		from.setEndLine(fromNode.getEndLine());
		from.setOffset(fromNode.getOffsetOnLine());
		from.setEnvironment(fromNode.getType() == OutlineNode.TYPE_ENVIRONMENT);
		String labelText = getLabelText(fromNode);
		from.setLabelText(labelText);
		EdgeContext context = new EdgeContextImpl(edgeType);
		edge.setContext(context);
		addEdge(edge, from, to);
	}

	private void addEdge(Edge edge, final Node from, final Node to) {
		Node foundFrom = null;
		Node foundTo = null;
		if (this.hypergraph.containsVertex(from)) {
			foundFrom = findVertice(from);
		}
		if (this.hypergraph.containsVertex(to)) {
			foundTo = findVertice(to);
		}
		this.hypergraph.addEdge(edge, foundFrom != null ? foundFrom : from,
				foundTo != null ? foundTo : to);
	}

	private Node findVertice(Node node) {
		Collection<Node> vertices = this.hypergraph.getVertices();
		for (Node cur : vertices) {
			if (cur.equals(node)) {
				return cur;
			}
		}
		throw new RuntimeException("node not found: " + node);
	}

	/**
	 * get label text of given node
	 * 
	 * @param fromNode
	 * @return
	 */
	private String getLabelText(OutlineNode fromNode) {
		if (fromNode.getChildren() != null) {
			for (OutlineNode child : fromNode.getChildren()) {
				if (child.getType() == OutlineNode.TYPE_LABEL) {
					return child.getName();
				}
			}
		}
		return null;
	}

	private void addReferenceEdges(List<DocumentReference> references, Node to) {
		for (DocumentReference reference : references) {
			OutlineNode parent = getReferenceParent(reference);
			if (parent != null) {
				addInverseEdge(parent, to, EdgeType.REFERS_TO);
			}
		}
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

		OutlineNode documentRoot = getModel().getDocumentRoot();
		if (reference.startLine >= documentRoot.getBeginLine()
				&& reference.startLine <= documentRoot.getEndLine()) {
			return documentRoot;
		}
		return null; // then the reference is outer
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
