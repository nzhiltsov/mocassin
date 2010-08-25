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

	private List<Edge<Node, Node>> graph;

	public ArxmlivParserImpl() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		this.documentBuilder = factory.newDocumentBuilder();
	}

	@Override
	public List<Edge<Node, Node>> getGraph() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void load(InputStream inputStream) throws Exception {
		Document doc = getDocumentBuilder().parse(inputStream);
		NodeList structureNodes = getXpathSearcher().findStructureNodes(doc);
		structureNodes.item(0);
		// getXpathSearcher().findReferencesMap(doc);

	}

	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}

	public XPathSearcher getXpathSearcher() {
		return xpathSearcher;
	}

}
