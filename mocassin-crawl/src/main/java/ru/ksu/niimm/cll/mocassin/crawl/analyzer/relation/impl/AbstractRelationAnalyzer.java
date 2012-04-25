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

	protected Graph<StructuralElement, Reference> graph;

	protected AbstractRelationAnalyzer(
			StructuralElementSearcher structuralElementSearcher) {
		this.structuralElementSearcher = structuralElementSearcher;
	}

	protected void addEdge(Reference edge, final StructuralElement from,
			final StructuralElement to) {
		StructuralElement foundFrom = null;
		StructuralElement foundTo = null;
		if (this.graph.containsVertex(from)) {
			foundFrom = findVertice(from);
		}
		if (this.graph.containsVertex(to)) {
			foundTo = findVertice(to);
		}
		this.graph.addEdge(edge, foundFrom != null ? foundFrom : from,
				foundTo != null ? foundTo : to);
	}

	protected StructuralElement findVertice(StructuralElement node) {
		Collection<StructuralElement> vertices = this.graph.getVertices();
		for (StructuralElement cur : vertices) {
			if (cur.equals(node)) {
				return cur;
			}
		}
		throw new RuntimeException("node not found: " + node);
	}
}