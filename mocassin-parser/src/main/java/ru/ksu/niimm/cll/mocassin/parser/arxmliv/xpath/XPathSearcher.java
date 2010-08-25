package ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XPathSearcher {
	/**
	 * return reference list
	 * 
	 * @param given
	 *            document
	 * @return
	 * @throws XPathExpressionException
	 */
	NodeList findReferences(Document doc) throws XPathExpressionException;

	/**
	 * return set of nodes that represent structural elements
	 * 
	 * @param doc
	 * @return
	 * @throws XPathExpressionException
	 */
	NodeList findStructureNodes(Document doc) throws XPathExpressionException;
}
