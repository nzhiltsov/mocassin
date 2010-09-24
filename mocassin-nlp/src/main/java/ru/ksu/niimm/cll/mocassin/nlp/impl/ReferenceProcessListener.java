package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Document;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public interface ReferenceProcessListener {
	void onReferenceFinish(Document document, List<Reference> references);
}
