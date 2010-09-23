package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

import gate.Document;

public interface ReferenceProcessListener {
	void onReferenceFinish(Document document, List<Reference> references);
}
