package ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
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
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl.NodePositionComparator;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.NewtheoremCommand;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardMathEnvironments;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardMetadataEnvironments;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardStyleEnvironments;

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
			logger
					.log(Level.SEVERE,
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

			String documentNodeId = String.format(NODE_ID_FORMAT, documentRoot
					.getBeginLine(), documentRoot.getOffsetOnLine());
			String documentNodeName = documentRoot.getName();
			Node documentRootNode = new NodeImpl.Builder(documentNodeId,
					documentNodeName).beginLine(documentRoot.getBeginLine())
					.endLine(documentRoot.getEndLine()).offset(
							documentRoot.getOffsetOnLine())
					.isEnvironment(false).numbered(false).build();
			addEdge(documentRootNode, treeItem, EdgeType.CONTAINS);
		}
		while (!stack.isEmpty()) {
			OutlineNode node = stack.pop();

			ArrayList<OutlineNode> children = node.getChildren();

			if (children != null) {
				String nodeId = String.format(NODE_ID_FORMAT, node
						.getBeginLine(), node.getOffsetOnLine());
				String nodeTitle = extractTitle(node);
				boolean isNumbered = getNumberedProperty(node);
				Node from = new NodeImpl.Builder(nodeId, extractName(node))
						.beginLine(node.getBeginLine()).endLine(
								node.getEndLine()).offset(
								node.getOffsetOnLine()).isEnvironment(
								node.getType() == OutlineNode.TYPE_ENVIRONMENT)
						.title(nodeTitle).numbered(isNumbered).build();
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
		extractTitles();
		return this.hypergraph;
	}

	private void extractTitles() {
		SortedSet<Node> sortedNodes = new TreeSet<Node>(
				new NodePositionComparator());
		sortedNodes.addAll(this.hypergraph.getVertices());
		int currentSectionNumber = 0;
		int currentEnvironmentNumber = 0;
		int currentSubsectionNumber = 0;
		for (Node node : sortedNodes) {
			String nodeName = node.getName();
			if (!node.isNumbered()
					|| StandardMetadataEnvironments.contains(nodeName)
					|| StandardMathEnvironments.contains(nodeName)
					|| StandardStyleEnvironments.contains(nodeName)
					|| nodeName.equals("proof") || nodeName.equals("document")
					|| nodeName.equals("abstract"))
				continue;
			if (nodeName.equals("section")) {
				currentSectionNumber++;
				node.setTitle(String.format("%d. %s", currentSectionNumber,
						node.getTitle()));
				currentEnvironmentNumber = 0;
				currentSubsectionNumber = 0;
			} else if (nodeName.equals("subsection")) {
				currentSubsectionNumber++;
				node.setTitle(String.format("%d.%d. %s", currentSectionNumber,
						currentSubsectionNumber, node.getTitle()));
			} else {
				currentEnvironmentNumber++;
				node.setTitle(String.format("%s %d.%d", node.getTitle(),
						currentSectionNumber, currentEnvironmentNumber));
			}
		}
	}

	private void addEdge(Node from, OutlineNode toNode, EdgeType edgeType) {
		Edge edge = new EdgeImpl();
		String childId = String.format(NODE_ID_FORMAT, toNode.getBeginLine(),
				toNode.getOffsetOnLine());
		String labelText = getLabelText(toNode);
		String nodeName = extractName(toNode);
		String nodeTitle = extractTitle(toNode);
		boolean isNumbered = getNumberedProperty(toNode);
		Node to = new NodeImpl.Builder(childId, nodeName).beginLine(
				toNode.getBeginLine()).endLine(toNode.getEndLine()).offset(
				toNode.getOffsetOnLine()).isEnvironment(
				toNode.getType() == OutlineNode.TYPE_ENVIRONMENT).labelText(
				labelText).title(nodeTitle).numbered(isNumbered).build();
		EdgeContext context = new EdgeContextImpl(edgeType);
		edge.setContext(context);
		addEdge(edge, from, to);
	}

	private String extractName(OutlineNode node) {
		String nodeName;
		switch (node.getType()) {
		case OutlineNode.TYPE_SECTION:
			nodeName = "section";
			break;
		case OutlineNode.TYPE_SUBSECTION:
			nodeName = "subsection";
			break;
		case OutlineNode.TYPE_SUBSUBSECTION:
			nodeName = "subsubsection";
			break;
		default: {
			nodeName = node.getName();
			NewtheoremCommand foundCommand = Iterables.find(getModel()
					.getNewtheorems(), new NewtheoremCommand.KeyPredicate(
					nodeName), null);
			if (foundCommand != null) {
				nodeName = foundCommand.getTitle();
			}
		}
		}
		return nodeName;
	}

	private String extractTitle(OutlineNode node) {
		int nodeType = node.getType();
		String nodeName = node.getName();
		String nodeTitle = null;
		if (nodeType == OutlineNode.TYPE_SECTION
				|| nodeType == OutlineNode.TYPE_SUBSECTION
				|| nodeType == OutlineNode.TYPE_SUBSUBSECTION) {

			nodeTitle = nodeName;
		} else {
			NewtheoremCommand foundCommand = Iterables.find(getModel()
					.getNewtheorems(), new NewtheoremCommand.KeyPredicate(
					nodeName), null);
			if (foundCommand != null) {
				nodeTitle = foundCommand.getTitle();
			}
		}
		return nodeTitle;
	}

	private boolean getNumberedProperty(OutlineNode node) {
		int nodeType = node.getType();
		if (nodeType == OutlineNode.TYPE_SECTION
				|| nodeType == OutlineNode.TYPE_SUBSECTION
				|| nodeType == OutlineNode.TYPE_SUBSUBSECTION)
			return true; // TODO: not accurate!!

		String nodeName = node.getName();
		if (nodeName.endsWith("*"))
			return false;
		NewtheoremCommand foundCommand = Iterables.find(getModel()
				.getNewtheorems(),
				new NewtheoremCommand.KeyPredicate(nodeName), null);
		if (foundCommand != null) {
			return foundCommand.isNumbered();
		}
		return true;
	}

	private void addInverseEdge(OutlineNode fromNode, Node to, EdgeType edgeType) {
		Edge edge = new EdgeImpl();
		String childId = String.format(NODE_ID_FORMAT, fromNode.getBeginLine(),
				fromNode.getOffsetOnLine());
		String nodeName = extractName(fromNode);
		String labelText = getLabelText(fromNode);
		String nodeTitle = extractTitle(fromNode);
		boolean isNumbered = getNumberedProperty(fromNode);
		Node from = new NodeImpl.Builder(childId, nodeName).beginLine(
				fromNode.getBeginLine()).endLine(fromNode.getEndLine()).offset(
				fromNode.getOffsetOnLine()).isEnvironment(
				fromNode.getType() == OutlineNode.TYPE_ENVIRONMENT).labelText(
				labelText).title(nodeTitle).numbered(isNumbered).build();
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
