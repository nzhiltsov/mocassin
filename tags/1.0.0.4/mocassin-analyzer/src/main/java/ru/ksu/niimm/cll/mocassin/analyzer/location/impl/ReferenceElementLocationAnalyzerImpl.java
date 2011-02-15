package ru.ksu.niimm.cll.mocassin.analyzer.location.impl;

import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public class ReferenceElementLocationAnalyzerImpl implements
		ReferenceElementLocationAnalyzer {

	@Override
	public ReferenceElementLocationInfo analyze(Reference reference) {
		long documentSize = reference.getDocument().getSize();
		float normalizedStartDistance = ((float) reference.getFrom().getStart() - reference
				.getTo().getStart())
				/ documentSize;
		float normalizedEndDistance = ((float) reference.getFrom().getEnd() - reference
				.getTo().getEnd())
				/ documentSize;
		return new ReferenceElementLocationInfoImpl(reference,
				normalizedStartDistance, normalizedEndDistance);
	}

}
