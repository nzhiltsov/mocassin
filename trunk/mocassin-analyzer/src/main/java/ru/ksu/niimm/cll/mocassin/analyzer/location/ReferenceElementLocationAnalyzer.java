package ru.ksu.niimm.cll.mocassin.analyzer.location;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public interface ReferenceElementLocationAnalyzer {
	ReferenceElementLocationInfo analyze(Reference reference);
}
