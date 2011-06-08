package ru.ksu.niimm.cll.mocassin.nlp;

import gate.Document;

@Deprecated
public interface AnnotationAnalyzer {
	ReferenceContext retrieveReferenceContext(Document document);

}
