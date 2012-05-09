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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl;

import java.util.Collection;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElementSearcher;

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
