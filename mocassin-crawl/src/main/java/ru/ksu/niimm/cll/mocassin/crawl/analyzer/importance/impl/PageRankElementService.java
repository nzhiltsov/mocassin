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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.impl;

import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.ImportantElementService;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public class PageRankElementService implements ImportantElementService {
	private static final double PROBABILITY_OF_RANDOM_JUMP = 0.2;
	private final PageRankScorer<StructuralElement, Reference> pageRankScorer = new PageRankScorer<StructuralElement, Reference>();

	@Override
	public Map<StructuralElement, Float> computeImportanceRanks(
			Graph<StructuralElement, Reference> graph) {
		return pageRankScorer.computePageRanks(graph,
				PROBABILITY_OF_RANDOM_JUMP);
	}

}
