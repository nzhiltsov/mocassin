package ru.ksu.niimm.cll.mocassin.crawl.analyzer.location;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface ReferenceElementLocationAnalyzer {
	ReferenceElementLocationInfo analyze(
			Graph<StructuralElement, Reference> graph, Reference reference);
}
