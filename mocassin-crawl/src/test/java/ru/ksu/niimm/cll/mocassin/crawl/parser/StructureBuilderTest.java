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
package ru.ksu.niimm.cll.mocassin.crawl.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;
import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.impl.NodeImpl.NodePositionComparator;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Node;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Parser;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Hypergraph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class StructureBuilderTest {
	@Inject
	private Parser parser;
	@Inject
	private StructureBuilder structureBuilder;

	private LatexDocumentModel model;

	@Before
	public void init() throws LexerException, IOException {
		InputStream in = new FileInputStream("/opt/mocassin/tex/ivm18.tex");
		this.model = parser.parse("ivm18", in, "cp866", true);
	}

	@Test
	public void testGraphEdges() {
		Hypergraph<Node, Edge> graph = getStructureBuilder()
				.buildStructureGraph(this.model);
		Assert.assertTrue(graph.getEdgeCount() > 0);
		for (Edge edge : graph.getEdges()) {
			Node from = graph.getSource(edge);
			Node to = graph.getDest(edge);
			System.out.println(String.format("%s | %s | %s", from.getName(),
					to.getName(), edge.getContext().getEdgeType()));
		}
		System.out.println("***");

	}

	@Test
	public void testGraphNodes() {
		Hypergraph<Node, Edge> graph = getStructureBuilder()
				.buildStructureGraph(this.model);
		Assert.assertTrue(graph.getVertexCount() > 0);

		SortedSet<Node> sortedNodes = new TreeSet<Node>(
				new NodePositionComparator());
		sortedNodes.addAll(graph.getVertices());
		for (Node node : sortedNodes) {
			System.out.println(String.format("%s \"%s\" page:%d", node,
					node.getTitle() != null ? node.getTitle() : "", node.getPdfPageNumber()));
		}

		System.out.println("***");

	}

	public StructureBuilder getStructureBuilder() {
		return structureBuilder;
	}

}
