package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.swizzle.stream.FixedTokenReplacementInputStream;
import org.codehaus.swizzle.stream.ReplaceStringInputStream;

import net.sourceforge.texlipse.model.OutlineNode;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.Builder;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.BuildersProvider;

import com.google.inject.Inject;

public class LatexParserImpl implements Parser {
	@Inject
	private TreeParser treeParser;
	@Inject
	private BuildersProvider analyzersProvider;

	private LatexDocumentModel model;

	@Override
	public List<Edge<Node, Node>> getGraph() {
		List<Edge<Node, Node>> graph = new ArrayList<Edge<Node, Node>>();
		for (Builder analyzer : getAnalyzers()) {
			List<Edge<Node, Node>> edges = analyzer.analyze(getModel());
			merge(graph, edges);
		}
		return graph;
	}

	@Override
	public List<Node> getNodes() {
		List<Node> graphNodes = new ArrayList<Node>();

		List<OutlineNode> tree = getModel().getTree();

		LinkedList<OutlineNode> queue = new LinkedList<OutlineNode>();
		queue.addAll(tree);
		while (!queue.isEmpty()) {
			OutlineNode node = queue.pop();
			if (node.getType() == OutlineNode.TYPE_LABEL)
				continue;
			String nodeId = String.format("%d:%d", node.getBeginLine(), node
					.getOffsetOnLine());

			String treeNodeName = generateNodeName(node);

			Node treeNode = new NodeImpl(nodeId, treeNodeName);
			if (!graphNodes.contains(treeNode)) {
				graphNodes.add(treeNode);
			}

			ArrayList<OutlineNode> children = node.getChildren();

			if (children != null) {

				for (OutlineNode child : children) {
					queue.add(child);
				}
			}
		}
		return graphNodes;
	}

	private String generateNodeName(OutlineNode node) {
		String treeNodeName;
		switch (node.getType()) {
		case OutlineNode.TYPE_SECTION:
			treeNodeName = ArxmlivStructureElementTypes.SECTION.toString();
			break;
		case OutlineNode.TYPE_SUBSECTION:
			treeNodeName = ArxmlivStructureElementTypes.SUBSECTION.toString();
			break;
		default:
			treeNodeName = node.getName();
		}
		return treeNodeName;
	}

	/**
	 * merge graph with given edges list
	 * 
	 * @param graph
	 * @param edges
	 */
	private void merge(List<Edge<Node, Node>> graph,
			List<Edge<Node, Node>> edges) {
		// TODO : implement merge process
		graph.addAll(edges);
	}

	@Override
	public void load(final InputStream inputStream) throws Exception {

		final PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(out);

		new Thread(new Runnable() {
			public void run() {
				try {
					InputStream replaceInputStream = new ReplaceStringInputStream(
							inputStream, "\\newtheorem{", "\\newcommand{\\");
					try {
						int b;
						while ((b = replaceInputStream.read()) != -1) {
							out.write(b);
						}
					} finally {
						out.flush();
						out.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		try {
			Reader reader = new InputStreamReader(in);
			LatexDocumentModel parsedModel = getTreeParser().parseTree(reader);
			setModel(parsedModel);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

	private LatexDocumentModel getModel() {
		return model;
	}

	private void setModel(LatexDocumentModel model) {
		this.model = model;
	}

	private TreeParser getTreeParser() {
		return treeParser;
	}

	private BuildersProvider getAnalyzersProvider() {
		return analyzersProvider;
	}

	public List<Builder> getAnalyzers() {
		return getAnalyzersProvider().get();
	}

}
