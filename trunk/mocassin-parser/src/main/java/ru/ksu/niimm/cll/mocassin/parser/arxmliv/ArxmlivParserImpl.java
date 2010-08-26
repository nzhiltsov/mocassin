package ru.ksu.niimm.cll.mocassin.parser.arxmliv;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.XPathSearcher;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeContextImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl;

import com.google.inject.Inject;

public class ArxmlivParserImpl implements Parser {

	private DocumentBuilder documentBuilder;
	@Inject
	private XPathSearcher xpathSearcher;

	private NodeList referenceNodes;

	private NodeList structureNodes;

	public ArxmlivParserImpl() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		this.documentBuilder = factory.newDocumentBuilder();
	}

	@Override
	public List<Edge<Node, Node>> getGraph() {
		List<Edge<Node, Node>> graph = new ArrayList<Edge<Node, Node>>();
		for (int i = 0; i < getReferenceNodes().getLength(); i++) {
			org.w3c.dom.Node refNode = getReferenceNodes().item(i);
			org.w3c.dom.Node labelAttr = refNode.getAttributes().getNamedItem(
					ArxmlivFormatConstants.LABEL_REF_ATTRIBUTE_NAME);
			org.w3c.dom.Node toNode = findStructureNodeByLabel(labelAttr
					.getTextContent());
			if (toNode == null)
				continue;
			org.w3c.dom.Node fromNode = findEnclosingStructureNode(refNode);
			if (fromNode == null)
				continue;
			Edge<Node, Node> edge = createEdge(fromNode, toNode, refNode);
			graph.add(edge);
		}
		return graph;
	}

	@Override
	public void load(InputStream inputStream) throws Exception {
		Document doc = getDocumentBuilder().parse(inputStream);
		this.structureNodes = getXpathSearcher().findStructureNodes(doc);
		this.referenceNodes = getXpathSearcher().findReferences(doc);
	}

	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}

	public XPathSearcher getXpathSearcher() {
		return xpathSearcher;
	}

	public NodeList getReferenceNodes() {
		return referenceNodes;
	}

	public NodeList getStructureNodes() {
		return structureNodes;
	}

	private org.w3c.dom.Node findStructureNodeByLabel(String label) {
		for (int i = 0; i < getStructureNodes().getLength(); i++) {
			org.w3c.dom.Node node = getStructureNodes().item(i);
			org.w3c.dom.Node labelsAttr = node.getAttributes().getNamedItem(
					ArxmlivFormatConstants.LABEL_ATTRIBUTE_NAME);
			if (labelsAttr == null)
				continue;
			String text = labelsAttr.getTextContent();
			if (text.equals(label)) {
				return node;
			}
		}
		return null;
	}

	private org.w3c.dom.Node findEnclosingStructureNode(org.w3c.dom.Node refNode) {
		org.w3c.dom.Node parent = refNode.getParentNode();
		while (parent != null) {
			if (ArxmlivStructureElementTypes.hasName(parent.getLocalName())) {
				return parent;
			}
			parent = parent.getParentNode();
		}
		return null;
	}

	private static String generateId(org.w3c.dom.Node node) {
		Stack<String> path = new Stack<String>();
		path.push(node.getLocalName());
		org.w3c.dom.Node parent = node.getParentNode();
		while (parent != null) {
			path.push(parent.getLocalName());
			parent = parent.getParentNode();
		}
		StringBuffer sb = new StringBuffer();
		while (!path.isEmpty()) {
			String pathName = path.pop();
			if (pathName != null) {
				sb.append("/");
				sb.append(pathName);
			}
		}
		return String.format("%s@%s", sb.toString(), Integer.toHexString(node
				.hashCode()));
	}

	private Edge<Node, Node> createEdge(org.w3c.dom.Node fromNode,
			org.w3c.dom.Node toNode, org.w3c.dom.Node refNode) {
		Edge<Node, Node> edge = new EdgeImpl();
		Node from = convertNode(fromNode);
		Node to = convertNode(toNode);
		EdgeContext edgeContext = new EdgeContextImpl(EdgeType.REFERS_TO);
		String aroundText = extractReferenceAroundText(refNode);
		edgeContext.setAroundText(aroundText);
		edge.connect(from, to, edgeContext);
		return edge;
	}

	private static String extractReferenceAroundText(org.w3c.dom.Node refNode) {
		org.w3c.dom.Node parent = refNode.getParentNode();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			org.w3c.dom.Node child = parent.getChildNodes().item(i);
			String text = child != refNode ? child.getTextContent() : "<ref>";
			sb.append(text);
		}
		return sb.toString();
	}

	private Node convertNode(org.w3c.dom.Node node) {
		String id = generateId(node);
		String localName = node.getLocalName();
		if (ArxmlivStructureElementTypes.THEOREM.toString().equals(localName)) {
			org.w3c.dom.Node classAttr = node.getAttributes().getNamedItem(
					ArxmlivFormatConstants.CLASS_ATTRIBUTE_NAME);
			localName = classAttr.getTextContent();
		}

		Node n = new NodeImpl(id, localName);
		org.w3c.dom.Node labelAttr = node.getAttributes().getNamedItem(
				ArxmlivFormatConstants.LABEL_ATTRIBUTE_NAME);
		if (labelAttr != null) {
			n.setLabelText(labelAttr.getTextContent());
		}
		return n;
	}
}
