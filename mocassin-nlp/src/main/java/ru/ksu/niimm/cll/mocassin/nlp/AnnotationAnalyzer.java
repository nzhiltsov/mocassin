package ru.ksu.niimm.cll.mocassin.nlp;

import gate.Document;

public interface AnnotationAnalyzer {
	ReferenceContext retrieveReferenceContext(Document document);

}
