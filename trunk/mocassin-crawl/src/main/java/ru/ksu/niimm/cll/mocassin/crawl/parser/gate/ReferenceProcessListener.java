package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface ReferenceProcessListener {
	void onReferenceFinish(ParsedDocument document,
			Graph<StructuralElement, Reference> graph);
}
