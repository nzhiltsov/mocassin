package ru.ksu.niimm.cll.mocassin.nlp.util;

import gate.Annotation;
import gate.Document;

import java.util.List;

public interface AnnotationUtil {

	List<String> getTokensForAnnotation(Document document,
			Annotation annotation, boolean useStemming);

}