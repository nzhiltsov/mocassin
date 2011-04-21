package ru.ksu.niimm.cll.mocassin.parser.arxmliv;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.XPathSearcher;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl;

import com.google.inject.Inject;

@Deprecated
public class ArxmlivParserImpl {

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
		return String.format("%s:%s", sb.toString(), Integer.toHexString(node
				.hashCode()));
	}

	private static String extractAroundReferenceText(org.w3c.dom.Node refNode)
			throws TransformerFactoryConfigurationError, TransformerException {
		org.w3c.dom.Node parent = refNode.getParentNode();
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		transformer.transform(new DOMSource(parent), new StreamResult(writer));
		String xml = writer.toString();
		return xml;
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
