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
