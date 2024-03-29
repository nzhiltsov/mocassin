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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.util.StreamHandler;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class GateCitationSearcher implements CitationSearcher {
    private final String ARXMLIV_MARKUP_NAME;
    private final String ARXMLIV_CITE_ANNOTATION_NAME;
    private final String TOKEN_ANNOTATION_NAME;
    @InjectLogger
    private Logger logger;

    private final AnnotationUtil annotationUtil;

    private final BibliographyExtractor bibliographyExtractor;

    @Inject
    GateCitationSearcher(
	    AnnotationUtil annotationUtil,
	    BibliographyExtractor bibliographyExtractor,
	    @Named("arxmliv.markup.name") String arxmlivMarkupName,
	    @Named("arxmliv.cite.annotation.name") String arxmlivCiteAnnotationName,
	    @Named("token.annotation.name") String tokenAnnotationName) {
	this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
	this.ARXMLIV_CITE_ANNOTATION_NAME = arxmlivCiteAnnotationName;
	this.TOKEN_ANNOTATION_NAME = tokenAnnotationName;
	this.annotationUtil = annotationUtil;
	this.bibliographyExtractor = bibliographyExtractor;
    }

    @Override
    public List<String> getCitationSentences(Document gateDocument,
	    String paperUrl) {
	String documentId = StringUtil.extractMathnetKeyFromURI(paperUrl);
	AnnotationSet citations = gateDocument.getAnnotations(
		ARXMLIV_MARKUP_NAME).get(ARXMLIV_CITE_ANNOTATION_NAME);
	return extractSentences(gateDocument, citations, documentId);

    }

    @Override
    public LinkedList<Citation> getCitations(Document gateDocument,
	    String paperUrl) {
	LinkedList<Citation> citations = new LinkedList<Citation>();
	String documentId = StringUtil.extractMathnetKeyFromURI(paperUrl);
	AnnotationSet tokenSet = gateDocument.getAnnotations(
		GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME).get(
		TOKEN_ANNOTATION_NAME);
	ArrayList<Annotation> tokenList = new ArrayList<Annotation>(tokenSet);
	Collections.sort(tokenList, new OffsetComparator());
	StreamHandler<Annotation> streamHandler = new StreamHandler<Annotation>(
		tokenList, new OpenCitationPredicate(),
		new NumberCitationPredicate(), new ClosingCitationPredicate());
	List<Annotation> citationNumberAnnotations = streamHandler.process();
	for (Annotation a : citationNumberAnnotations) {
	    String sentence = getSentence(gateDocument, a, documentId);
	    String numberStr = (String) a.getFeatures().get(
		    GateFormatConstants.TOKEN_FEATURE_NAME);
	    Citation citation = new Citation.Builder(documentId)
		    .anchorText(sentence).number(Integer.parseInt(numberStr))
		    .build();
	    citations.add(citation);
	}
	return citations;

    }

    @SuppressWarnings("unchecked")
    private List<String> extractSentences(Document gateDocument,
	    AnnotationSet citations, String documentId) {
	List<Annotation> sortedCitations = new ArrayList<Annotation>(citations);
	Collections.sort(sortedCitations);
	List<String> citationSentences = new LinkedList<String>();
	for (Annotation citation : sortedCitations) {
	    String sentence = getSentence(gateDocument, citation, documentId);
	    citationSentences.add(sentence);
	}
	return citationSentences;
    }

    private String getSentence(Document gateDocument, Annotation citation,
	    String documentId) {
	Annotation enclosingSentence = annotationUtil.getEnclosingSentence(
		gateDocument, citation);
	String numberStr = (String) citation.getFeatures().get(
		GateFormatConstants.TOKEN_FEATURE_NAME);
	int number = Integer.parseInt(numberStr);
	String toKey = bibliographyExtractor.getToKey(documentId, number);
	String sentence = annotationUtil
		.getTextContentsForAnnotationWithReplacements(gateDocument,
			enclosingSentence, citation, toKey != null ? toKey
				: numberStr);

	return sentence;
    }

    private static class OpenCitationPredicate implements Predicate<Annotation> {

	private static final String OPENING_CITATION_MARK = "[";

	@Override
	public boolean apply(Annotation input) {
	    String tokenValue = (String) input.getFeatures().get(
		    GateFormatConstants.TOKEN_FEATURE_NAME);
	    return tokenValue.equals(OPENING_CITATION_MARK);
	}

    }

    private static class NumberCitationPredicate implements
	    Predicate<Annotation> {
	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

	@Override
	public boolean apply(Annotation input) {
	    String tokenValue = (String) input.getFeatures().get(
		    GateFormatConstants.TOKEN_FEATURE_NAME);
	    return NUMBER_PATTERN.matcher(tokenValue).matches();
	}

    }

    private static class ClosingCitationPredicate implements
	    Predicate<Annotation> {

	private static final String CLOSING_CITATION_MARK = "]";

	@Override
	public boolean apply(Annotation input) {
	    String tokenValue = (String) input.getFeatures().get(
		    GateFormatConstants.TOKEN_FEATURE_NAME);
	    return tokenValue.equals(CLOSING_CITATION_MARK);
	}

    }
}
