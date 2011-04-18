package ru.ksu.niimm.cll.mocassin.nlp.util;

import gate.Annotation;
import gate.Document;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Token;

public interface AnnotationUtil {

	List<Token> getTokensForAnnotation(Document document,
			Annotation annotation, boolean useStemming);

}