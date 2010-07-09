package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.texlipse.model.OutlineNode;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.Analyzer;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.AnalyzersProvider;

import com.google.inject.Inject;

public class LatexParserImpl implements Parser {
	@Inject
	private TreeParser treeParser;
	@Inject
	private AnalyzersProvider analyzersProvider;

	private LatexDocumentModel model;

	@Override
	public List<Edge<Node, Node>> getGraph() {
		List<Edge<Node, Node>> graph = new ArrayList<Edge<Node, Node>>();
		for (Analyzer analyzer : getAnalyzers()) {
			List<Edge<Node, Node>> edges = analyzer.analyze(getModel());
			merge(graph, edges);
		}
		return graph;
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
	public void load(Reader reader) throws Exception {
		LatexDocumentModel parsedModel = getTreeParser().parseTree(reader);
		setModel(parsedModel);
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

	private AnalyzersProvider getAnalyzersProvider() {
		return analyzersProvider;
	}

	public List<Analyzer> getAnalyzers() {
		return getAnalyzersProvider().get();
	}

}