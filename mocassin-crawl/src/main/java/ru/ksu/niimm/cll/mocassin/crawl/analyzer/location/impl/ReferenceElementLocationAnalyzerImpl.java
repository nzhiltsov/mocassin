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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.impl;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.ReferenceElementLocationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.ReferenceElementLocationInfo;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public class ReferenceElementLocationAnalyzerImpl implements
		ReferenceElementLocationAnalyzer {

	@Override
	public ReferenceElementLocationInfo analyze(
			Graph<StructuralElement, Reference> graph, Reference reference) {
		long documentSize = reference.getDocument().getSize();
		StructuralElement from = graph.getSource(reference);
		StructuralElement to = graph.getDest(reference);
		float normalizedStartDistance = ((float) from.getGateStartOffset() - to
				.getGateStartOffset()) / documentSize;

		float normalizedEndDistance = ((float) from.getGateEndOffset() - to.getGateEndOffset())
				/ documentSize;
		return new ReferenceElementLocationInfoImpl(reference,
				normalizedStartDistance, normalizedEndDistance);
	}

}
