package ru.ksu.niimm.cll.mocassin.analyzer.location;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface ReferenceElementLocationAnalyzer {
	ReferenceElementLocationInfo analyze(
			Graph<StructuralElement, Reference> graph, Reference reference);
}
