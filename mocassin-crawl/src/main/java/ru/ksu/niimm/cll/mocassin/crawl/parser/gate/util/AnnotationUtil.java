/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Term;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;

public interface AnnotationUtil {

	List<Token> getTokensForAnnotation(Document document,
			Annotation annotation);

	String[] getPureTokensForAnnotation(Document document,
			Annotation annotation, boolean useStemming);

	/**
	 * extracts text tokens along with LaTeX expressions as separate tokens
	 * 
	 * @param document
	 * @param annotation
	 * @return
	 */
	String[] getTokensWithMathAnnotation(Document document,
			Annotation annotation);

	/**
	 * extracts text tokens along with math expressions templated according to a
	 * given symbol
	 * 
	 * @param document
	 * @param annotation
	 * @return
	 */
	String[] getTokensWithTemplatedMathAnnotations(Document document,
			Annotation annotation, char symbol);

	/**
	 * returns a string of concatenated tokens contained by a given annotation
	 * 
	 * @param document
	 * @param annotation
	 * @return
	 */
	String getTextContentsForAnnotation(Document document, Annotation annotation);

	/**
	 * returns a string of concatenated tokens contained by a given annotation
	 * with replacement of given annotation's occurrences with a given
	 * replacement string
	 * 
	 * @param document
	 * @param annotation
	 * @return
	 */
	String getTextContentsForAnnotationWithReplacements(Document document,
			Annotation annotation, Annotation annotationForReplace,
			String replacementString);

	/**
	 * returns the enclosing sentence annotation (if any, otherwise null) for a
	 * given annotation
	 * 
	 * @param annotation
	 * @return
	 */
	Annotation getEnclosingSentence(Document document, Annotation annotation);
	
	AnnotationSet getStructuralAnnotations(Document document);
}
