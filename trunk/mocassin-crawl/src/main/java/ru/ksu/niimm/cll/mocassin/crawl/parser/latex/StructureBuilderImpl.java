/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.OutlineNode;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.parser.impl.EdgeContextImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.impl.EdgeImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.impl.NodeImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * This class implements building a graph with labels/references and containment
 * relations as its edges.
 * 
 * @author Nikita Zhiltsov
 * 
 */
class StructureBuilderImpl implements StructureBuilder {
    private static final String NODE_ID_FORMAT = "%d_%d";
    @InjectLogger
    private Logger logger;
    private final Latex2PDFMapper latex2pdfMapper;

    @Inject
    private StructureBuilderImpl(Latex2PDFMapper latex2pdfMapper) {
	this.latex2pdfMapper = latex2pdfMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph<Node, Edge> buildStructureGraph(LatexDocumentModel parsedModel) {
	Graph<Node, Edge> hypergraph = new DirectedSparseMultigraph<Node, Edge>();
	if (parsedModel == null) {
	    logger.warn("The parsed model is null. An empty graph will be returned");
	    return hypergraph;
	}

	Stack<OutlineNode> stack = new Stack<OutlineNode>();
	List<OutlineNode> tree = parsedModel.getTree();
	OutlineNode documentRoot = parsedModel.getDocumentRoot();
	for (int i = tree.size() - 1; i >= 0; i--) {
	    OutlineNode treeItem = tree.get(i);
	    stack.push(treeItem);

	    String documentNodeId = String
		    .format(NODE_ID_FORMAT, documentRoot.getBeginLine(),
			    documentRoot.getOffsetOnLine());
	    String documentNodeName = documentRoot.getName();
	    Node documentRootNode = new NodeImpl.Builder(documentNodeId,
		    documentNodeName).beginLine(documentRoot.getBeginLine())
		    .endLine(documentRoot.getEndLine())
		    .offset(documentRoot.getOffsetOnLine())
		    .isEnvironment(false).numbered(false).build();
	    addEdge(hypergraph, documentRootNode, treeItem, EdgeType.CONTAINS,
		    parsedModel);
	}
	while (!stack.isEmpty()) {
	    OutlineNode node = stack.pop();

	    ArrayList<OutlineNode> children = node.getChildren();

	    if (children != null) {
		String nodeId = String.format(NODE_ID_FORMAT,
			node.getBeginLine(), node.getOffsetOnLine());
		String nodeTitle = extractTitle(node, parsedModel);
		boolean isNumbered = getNumberedProperty(node, parsedModel);
		Node from = new NodeImpl.Builder(nodeId, extractName(node,
			parsedModel))
			.beginLine(node.getBeginLine())
			.endLine(node.getEndLine())
			.offset(node.getOffsetOnLine())
			.isEnvironment(
				node.getType() == OutlineNode.TYPE_ENVIRONMENT)
			.title(nodeTitle).numbered(isNumbered).build();
		for (OutlineNode child : children) {
		    if (child.getType() == OutlineNode.TYPE_LABEL) {
			from.setLabelText(child.getName());
			List<DocumentReference> references = getReferencesForLabel(
				child, parsedModel);
			addReferenceEdges(hypergraph, references, from,
				parsedModel);
		    } else {
			addEdge(hypergraph, from, child, EdgeType.CONTAINS,
				parsedModel);
			stack.add(child);
		    }

		}
	    }
	}
	fillPageNumbers(hypergraph, parsedModel);
	return hypergraph;
    }

    private void fillPageNumbers(Graph<Node, Edge> hypergraph,
	    LatexDocumentModel model) {
	Collection<Node> nodes = hypergraph.getVertices();

	for (Node node : nodes) {
	    int pageNumber = 0;
	    for (int l = node.getBeginLine(); l <= node.getEndLine(); l++) {
		pageNumber = latex2pdfMapper.getPDFPageNumber(l,
			model.getDocId());
		if (pageNumber > 0)
		    break;
	    }
	    node.setPdfPageNumber(pageNumber);
	}
    }

    private void addEdge(Graph<Node, Edge> hypergraph, Node from,
	    OutlineNode toNode, EdgeType edgeType, LatexDocumentModel model) {
	Edge edge = new EdgeImpl();
	String childId = String.format(NODE_ID_FORMAT, toNode.getBeginLine(),
		toNode.getOffsetOnLine());
	String labelText = getLabelText(toNode);
	String nodeName = extractName(toNode, model);
	String nodeTitle = extractTitle(toNode, model);
	boolean isNumbered = getNumberedProperty(toNode, model);
	Node to = new NodeImpl.Builder(childId, nodeName)
		.beginLine(toNode.getBeginLine())
		.endLine(toNode.getEndLine())
		.offset(toNode.getOffsetOnLine())
		.isEnvironment(toNode.getType() == OutlineNode.TYPE_ENVIRONMENT)
		.labelText(labelText).title(nodeTitle).numbered(isNumbered)
		.build();
	EdgeContext context = new EdgeContextImpl(edgeType);
	edge.setContext(context);
	addEdge(hypergraph, edge, from, to);
    }

    private String extractName(OutlineNode node, LatexDocumentModel model) {
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
	    NewtheoremCommand foundCommand = Iterables.find(model
		    .getNewtheorems(), new NewtheoremCommand.KeyPredicate(
		    nodeName), null);
	    if (foundCommand != null) {
		nodeName = foundCommand.getTitle();
	    }
	}
	}
	return nodeName;
    }

    private String extractTitle(OutlineNode node, LatexDocumentModel model) {
	int nodeType = node.getType();
	String nodeName = node.getName();
	String nodeTitle = null;
	if (nodeType == OutlineNode.TYPE_SECTION
		|| nodeType == OutlineNode.TYPE_SUBSECTION
		|| nodeType == OutlineNode.TYPE_SUBSUBSECTION) {

	    nodeTitle = nodeName;
	} else {
	    NewtheoremCommand foundCommand = Iterables.find(model
		    .getNewtheorems(), new NewtheoremCommand.KeyPredicate(
		    nodeName), null);
	    if (foundCommand != null) {
		nodeTitle = foundCommand.getTitle();
	    } else {
		nodeTitle = nodeName;
	    }
	}
	return nodeTitle;
    }

    private boolean getNumberedProperty(OutlineNode node,
	    LatexDocumentModel model) {
	int nodeType = node.getType();
	if (nodeType == OutlineNode.TYPE_SECTION
		|| nodeType == OutlineNode.TYPE_SUBSECTION
		|| nodeType == OutlineNode.TYPE_SUBSUBSECTION)
	    return true; // TODO: not accurate!!

	String nodeName = node.getName();
	if (nodeName.endsWith("*"))
	    return false;
	NewtheoremCommand foundCommand = Iterables.find(model.getNewtheorems(),
		new NewtheoremCommand.KeyPredicate(nodeName), null);
	if (foundCommand != null) {
	    return foundCommand.isNumbered();
	}
	return true;
    }

    private void addInverseEdge(Graph<Node, Edge> hypergraph,
	    OutlineNode fromNode, Node to, EdgeType edgeType,
	    LatexDocumentModel model) {
	Edge edge = new EdgeImpl();
	String childId = String.format(NODE_ID_FORMAT, fromNode.getBeginLine(),
		fromNode.getOffsetOnLine());
	String nodeName = extractName(fromNode, model);
	String labelText = getLabelText(fromNode);
	String nodeTitle = extractTitle(fromNode, model);
	boolean isNumbered = getNumberedProperty(fromNode, model);
	Node from = new NodeImpl.Builder(childId, nodeName)
		.beginLine(fromNode.getBeginLine())
		.endLine(fromNode.getEndLine())
		.offset(fromNode.getOffsetOnLine())
		.isEnvironment(
			fromNode.getType() == OutlineNode.TYPE_ENVIRONMENT)
		.labelText(labelText).title(nodeTitle).numbered(isNumbered)
		.build();
	EdgeContext context = new EdgeContextImpl(edgeType);
	edge.setContext(context);
	addEdge(hypergraph, edge, from, to);
    }

    private void addEdge(Graph<Node, Edge> hypergraph, Edge edge,
	    final Node from, final Node to) {
	Node foundFrom = null;
	Node foundTo = null;
	if (hypergraph.containsVertex(from)) {
	    foundFrom = findVertice(hypergraph, from);
	}
	if (hypergraph.containsVertex(to)) {
	    foundTo = findVertice(hypergraph, to);
	}
	hypergraph.addEdge(edge, foundFrom != null ? foundFrom : from,
		foundTo != null ? foundTo : to);
    }

    private Node findVertice(Graph<Node, Edge> hypergraph, Node node) {
	Collection<Node> vertices = hypergraph.getVertices();
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

    private void addReferenceEdges(Graph<Node, Edge> hypergraph,
	    List<DocumentReference> references, Node to,
	    LatexDocumentModel model) {
	for (DocumentReference reference : references) {
	    OutlineNode parent = getReferenceParent(reference, model);
	    if (parent != null) {
		addInverseEdge(hypergraph, parent, to, EdgeType.REFERS_TO,
			model);
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
    private OutlineNode getReferenceParent(DocumentReference reference,
	    LatexDocumentModel model) {
	Stack<OutlineNode> stack = new Stack<OutlineNode>();

	for (OutlineNode root : model.getTree()) {
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

	OutlineNode documentRoot = model.getDocumentRoot();
	if (reference.startLine >= documentRoot.getBeginLine()
		&& reference.startLine <= documentRoot.getEndLine()) {
	    return documentRoot;
	}
	return null; // then the reference is outer
    }

    private List<DocumentReference> getReferencesForLabel(OutlineNode child,
	    LatexDocumentModel model) {

	PdfReferenceEntry label = getLabel(child, model);
	List<DocumentReference> refs = new ArrayList<DocumentReference>();
	Iterator<DocumentReference> iterator = model.getReferences().iterator();
	while (iterator.hasNext()) {
	    DocumentReference ref = iterator.next();
	    if (ref.getKey().equals(label.key())) {
		refs.add(ref);
	    } else if (!refs.isEmpty()) {
		break; // 'cause references list is ordered by 'key'
	    }
	}

	return refs;
    }

    private PdfReferenceEntry getLabel(OutlineNode child,
	    LatexDocumentModel model) {
	PdfReferenceEntry foundLabel = null;
	int i = 0;
	while (i <= model.getLabels().size() - 1) {
	    PdfReferenceEntry label = model.getLabels().get(i);
	    boolean found = label.key().equals(child.getName())
		    && label.startLine() == child.getBeginLine();
	    if (found) {
		foundLabel = label;
	    }
	    i++;
	}
	return foundLabel;
    }

}
