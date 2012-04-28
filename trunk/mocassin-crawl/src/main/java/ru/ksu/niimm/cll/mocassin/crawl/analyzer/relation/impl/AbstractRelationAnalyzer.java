package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl;

import java.util.Collection;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElementSearcher;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractRelationAnalyzer {

	@Inject
	protected final StructuralElementSearcher structuralElementSearcher;

	protected AbstractRelationAnalyzer(
			StructuralElementSearcher structuralElementSearcher) {
		this.structuralElementSearcher = structuralElementSearcher;
	}

	protected void addEdge(Graph<StructuralElement, Reference> graph,
			Reference edge, final StructuralElement from,
			final StructuralElement to) {
		StructuralElement foundFrom = null;
		StructuralElement foundTo = null;
		if (graph.containsVertex(from)) {
			foundFrom = findVertice(graph, from);
		}
		if (graph.containsVertex(to)) {
			foundTo = findVertice(graph, to);
		}
		graph.addEdge(edge, foundFrom != null ? foundFrom : from,
				foundTo != null ? foundTo : to);
	}

	protected StructuralElement findVertice(
			Graph<StructuralElement, Reference> graph, StructuralElement node) {
		Collection<StructuralElement> vertices = graph.getVertices();
		for (StructuralElement cur : vertices) {
			if (cur.equals(node)) {
				return cur;
			}
		}
		throw new RuntimeException("node not found: " + node);
	}
}