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

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.GraphTopologyAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.RelationFeatureInfo;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;

public class GraphTopologyAnalyzerImpl implements GraphTopologyAnalyzer {

	@Override
	public List<RelationFeatureInfo> extractCandidateFeatures(
			Graph<StructuralElement, Reference> graph) {
		throw new UnsupportedOperationException("not implemented yet");
	}

}
