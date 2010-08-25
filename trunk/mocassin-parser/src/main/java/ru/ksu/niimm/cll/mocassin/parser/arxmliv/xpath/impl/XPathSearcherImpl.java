package ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.XPathSearcher;

public class XPathSearcherImpl implements XPathSearcher {
	private static final String FIND_STRUCTURE_NODES_EXPRESSION;

	static {
		StringBuffer sb = new StringBuffer();
		int i = 1;
		for (ArxmlivStructureElementTypes type : ArxmlivStructureElementTypes
				.values()) {
			String part = String.format("//*[name()='%s']", type);
			sb.append(part);
			if (i != ArxmlivStructureElementTypes.values().length) {
				sb.append("|");
				i++;
			}
		}
		FIND_STRUCTURE_NODES_EXPRESSION = sb.toString();
	}

	private XPathFactory xpathFactory;

	public XPathSearcherImpl() {
		this.xpathFactory = XPathFactory.newInstance();
	}

	@Override
	public Map<Node, List<Node>> findReferencesMap(Document doc)
			throws XPathExpressionException {
		XPath findReferencesXPath = getXpathFactory().newXPath();
		XPathExpression findReferencesExpression = findReferencesXPath
				.compile("//*[name()='ref']");
		Object result = findReferencesExpression.evaluate(doc,
				XPathConstants.NODESET);
		NodeList referenceNodes = (NodeList) result;
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public NodeList findStructureNodes(Document doc)
			throws XPathExpressionException {
		XPath findStructureNodesXPath = getXpathFactory().newXPath();
		XPathExpression findStructureNodesExpression = findStructureNodesXPath
				.compile(FIND_STRUCTURE_NODES_EXPRESSION);
		Object result = findStructureNodesExpression.evaluate(doc,
				XPathConstants.NODESET);
		NodeList structureNodes = (NodeList) result;
		return structureNodes;
	}

	public XPathFactory getXpathFactory() {
		return xpathFactory;
	}

}
