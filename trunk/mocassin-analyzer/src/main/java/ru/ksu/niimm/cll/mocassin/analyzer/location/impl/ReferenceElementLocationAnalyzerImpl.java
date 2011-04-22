package ru.ksu.niimm.cll.mocassin.analyzer.location.impl;

import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public class ReferenceElementLocationAnalyzerImpl implements
		ReferenceElementLocationAnalyzer {

	@Override
	public ReferenceElementLocationInfo analyze(
			Graph<StructuralElement, Reference> graph, Reference reference) {
		long documentSize = reference.getDocument().getSize();
		StructuralElement from = graph.getSource(reference);
		StructuralElement to = graph.getDest(reference);
		float normalizedStartDistance = ((float) from.getStart() - to
				.getStart()) / documentSize;

		float normalizedEndDistance = ((float) from.getEnd() - to.getEnd())
				/ documentSize;
		return new ReferenceElementLocationInfoImpl(reference,
				normalizedStartDistance, normalizedEndDistance);
	}

}
