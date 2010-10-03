package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
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
	public void load(InputStream inputStream) throws Exception {
		Reader reader = new InputStreamReader(inputStream);
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

	private BuildersProvider getAnalyzersProvider() {
		return analyzersProvider;
	}

	public List<Builder> getAnalyzers() {
		return getAnalyzersProvider().get();
	}

}
