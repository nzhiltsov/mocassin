package ru.ksu.niimm.cll.mocassin.nlp.impl;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface ReferenceProcessListener {
	void onReferenceFinish(ParsedDocument document,
			Graph<StructuralElement, Reference> graph);
}
