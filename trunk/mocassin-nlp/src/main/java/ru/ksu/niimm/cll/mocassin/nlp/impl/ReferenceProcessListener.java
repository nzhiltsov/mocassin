package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public interface ReferenceProcessListener {
	void onReferenceFinish(ParsedDocument document, List<Reference> references);
}
