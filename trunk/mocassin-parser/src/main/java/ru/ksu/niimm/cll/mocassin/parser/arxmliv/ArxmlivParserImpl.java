package ru.ksu.niimm.cll.mocassin.parser.arxmliv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.XPathSearcher;

public class ArxmlivParserImpl implements Parser {
	private DocumentBuilder documentBuilder;
	@Inject
	private XPathSearcher xpathSearcher;

	private NodeList referenceNodes;

	private NodeList structureNodes;

	public ArxmlivParserImpl() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		this.documentBuilder = factory.newDocumentBuilder();
	}

	@Override
	public List<Edge<Node, Node>> getGraph() {
		for (int i = 0; i < getReferenceNodes().getLength(); i++) {
			org.w3c.dom.Node refNode = getReferenceNodes().item(i);
			org.w3c.dom.Node labelAttr = refNode.getAttributes().getNamedItem(
					"labelref");
			org.w3c.dom.Node toNode = findStructureNodeByLabel(labelAttr
					.getTextContent());
		}
		throw new UnsupportedOperationException("not yet implemented");
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

	public org.w3c.dom.Node findStructureNodeByLabel(String label) {
		for (int i = 0; i < getStructureNodes().getLength(); i++) {
			org.w3c.dom.Node node = getStructureNodes().item(i);
			org.w3c.dom.Node labelsAttr = node.getAttributes().getNamedItem(
					"labels");
			String text = labelsAttr.getTextContent();
			if (text.equals(label)) {
				return node;
			}
		}
		throw new RuntimeException(
				String
						.format(
								"The document is in inconsistent state. Couldn't find node with label: %s",
								label));
	}

}
