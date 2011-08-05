package ru.ksu.niimm.cll.mocassin.nlp.util;

import gate.Annotation;
import gate.Document;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Token;

public interface AnnotationUtil {

	List<Token> getTokensForAnnotation(Document document,
			Annotation annotation, boolean useStemming);

	String[] getPureTokensForAnnotation(Document document,
			Annotation annotation, boolean useStemming);

	/**
	 * extracts text tokens along with LaTeX expressions as separate tokens
	 * 
	 * @param document
	 * @param annotation
	 * @return
	 */
	String[] getTokensWithMathAnnotation(Document document, Annotation annotation);
	/**
	 * extracts text tokens along with math expressions templated according to a given symbol 
	 * 
	 * @param document
	 * @param annotation
	 * @return
	 */
	String[] getTokensWithTemplatedMathAnnotations(Document document, Annotation annotation, char symbol);

	/**
	 * returns a string of concatenated tokens contained by a given annotation
	 * 
	 * @param document
	 * @param annotation
	 * @return
	 */
	String getTextContentsForAnnotation(Document document, Annotation annotation);
	
	/**
	 * returns the enclosing sentence annotation (if any, otherwise null) for a given annotation
	 * 
	 * @param annotation
	 * @return
	 */
	Annotation getEnclosingSentence(Document document, Annotation annotation);
}