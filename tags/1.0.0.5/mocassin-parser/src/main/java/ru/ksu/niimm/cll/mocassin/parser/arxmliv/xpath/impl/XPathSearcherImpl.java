package ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.XPathSearcher;

public class XPathSearcherImpl implements XPathSearcher {
	private static final String FIND_REFERENCES_EXPRESSION = "//*[name()='ref']";
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

	private XPathExpression findReferencesExpression;

	private XPathExpression findStructureNodesExpression;

	public XPathSearcherImpl() throws XPathExpressionException {
		this.xpathFactory = XPathFactory.newInstance();
		XPath findReferencesXPath = getXpathFactory().newXPath();
		this.findReferencesExpression = findReferencesXPath
				.compile(FIND_REFERENCES_EXPRESSION);
		XPath findStructureNodesXPath = getXpathFactory().newXPath();
		this.findStructureNodesExpression = findStructureNodesXPath
				.compile(FIND_STRUCTURE_NODES_EXPRESSION);
	}

	@Override
	public NodeList findReferences(Document doc)
			throws XPathExpressionException {
		Object result = getFindReferencesExpression().evaluate(doc,
				XPathConstants.NODESET);
		NodeList referenceNodes = (NodeList) result;
		return referenceNodes;
	}

	@Override
	public NodeList findStructureNodes(Document doc)
			throws XPathExpressionException {

		Object result = getFindStructureNodesExpression().evaluate(doc,
				XPathConstants.NODESET);
		NodeList structureNodes = (NodeList) result;
		return structureNodes;
	}

	public XPathFactory getXpathFactory() {
		return xpathFactory;
	}

	public XPathExpression getFindReferencesExpression() {
		return findReferencesExpression;
	}

	public XPathExpression getFindStructureNodesExpression() {
		return findStructureNodesExpression;
	}

}
