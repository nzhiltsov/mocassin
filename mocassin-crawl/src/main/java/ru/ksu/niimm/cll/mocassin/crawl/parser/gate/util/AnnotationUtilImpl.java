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

import static java.lang.String.format;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Formula;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.MathExpression;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Term;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Variable;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.TokenImpl;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AnnotationUtilImpl implements AnnotationUtil {
    private static final String FREE_EXPRESSION_MARK = ".Ex";

    private final String TOKEN_ANNOTATION_NAME;

    private final String ARXMLIV_MATH_ANNOTATION_NAME;

    private final String ARXMLIV_MATH_TEX_ANNOTATION_NAME;

    private final String ARXMLIV_MARKUP_NAME;

    private final String SPACE_TOKEN_ANNOTATION_NAME;

    private final String SENTENCE_ANNOTATION_NAME;

    private static final Set<String> NAME_SET = ArxmlivStructureElementTypes
	    .toNameSet();

    private final String DOMAIN_ONTOLOGY_URI;

    private final String NORMALIZED_FORM_ANNOTATION_NAME;

    private final String TERM_ANNOTATION_NAME;
    @InjectLogger
    private Logger logger;

    @Inject
    AnnotationUtilImpl(
	    @Named("token.annotation.name") String tokenAnnotationName,
	    @Named("arxmliv.math.annotation.name") String arxmlivMathAnnotationName,
	    @Named("arxmliv.math.tex.annotation.name") String arxmlivMathTexAnnotationName,
	    @Named("arxmliv.markup.name") String arxmlivMarkupName,
	    @Named("space.token.annotation.name") String spaceTokenAnnotationName,
	    @Named("sentence.annotation.name") String sentenceAnnotationName,
	    @Named("term.annotation.name") String termAnnotationName,
	    @Named("domain.ontology.uri") String domainOntologyURI,
	    @Named("normalized.form.annotation.name") String normalizedFormAnnotationName) {
	this.TOKEN_ANNOTATION_NAME = tokenAnnotationName;
	this.ARXMLIV_MATH_ANNOTATION_NAME = arxmlivMathAnnotationName;
	this.ARXMLIV_MATH_TEX_ANNOTATION_NAME = arxmlivMathTexAnnotationName;
	this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
	this.SPACE_TOKEN_ANNOTATION_NAME = spaceTokenAnnotationName;
	this.SENTENCE_ANNOTATION_NAME = sentenceAnnotationName;
	this.DOMAIN_ONTOLOGY_URI = domainOntologyURI;
	this.NORMALIZED_FORM_ANNOTATION_NAME = normalizedFormAnnotationName;
	this.TERM_ANNOTATION_NAME = termAnnotationName;
    }

    @Override
    public AnnotationSet getStructuralAnnotations(Document document) {
	AnnotationSet annotationSet = document
		.getAnnotations(ARXMLIV_MARKUP_NAME);
	AnnotationSet equations = document.getAnnotations(ARXMLIV_MARKUP_NAME)
		.get("equation");
	Set<Annotation> equationsForRemove = new HashSet<Annotation>();
	for (Annotation equation : equations) {
	    AnnotationSet coveringGroups = annotationSet.getCovering(
		    "equationgroup", equation.getStartNode().getOffset(),
		    equation.getEndNode().getOffset());
	    String xmlIdAttr = (String) equation.getFeatures().get("xml:id");
	    if (!coveringGroups.isEmpty() || xmlIdAttr != null
		    && xmlIdAttr.contains(FREE_EXPRESSION_MARK)) {
		equationsForRemove.add(equation);
	    }
	}
	annotationSet.removeAll(equationsForRemove);
	return annotationSet.get(NAME_SET);
    }

    @Override
    public Annotation getEnclosingSentence(Document document,
	    Annotation annotation) {
	AnnotationSet sentenceSet = document.getAnnotations(
		GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME).getCovering(
		SENTENCE_ANNOTATION_NAME,
		annotation.getStartNode().getOffset(),
		annotation.getEndNode().getOffset());
	if (sentenceSet.size() == 0) {
	    AnnotationSet allSentences = document.getAnnotations(
		    GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME).get(
		    SENTENCE_ANNOTATION_NAME);
	    long distance = Long.MAX_VALUE;
	    Annotation closestSentence = null;
	    for (Annotation sentence : allSentences) {
		long endDistance = Math.abs(sentence.getEndNode().getOffset()
			- annotation.getStartNode().getOffset());
		long startDistance = Math.abs(sentence.getStartNode()
			.getOffset() - annotation.getStartNode().getOffset());
		long minDistance = Math.min(endDistance, startDistance);
		if (minDistance < distance) {
		    closestSentence = sentence;
		    distance = minDistance;
		}
	    }
	    if (closestSentence == null) {
		throw new RuntimeException(String.format(
			"couldn't locate sentence for annotation with id='%s'",
			annotation.getId()));
	    } else {
		return closestSentence;
	    }
	}
	return sentenceSet.iterator().next();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.ksu.niimm.cll.mocassin.nlp.util.impl.AnnotationUtil#getTokensForAnnotation
     * (gate.Document, gate.Annotation)
     */
    public List<Token> getTokensForAnnotation(Document document,
	    Annotation annotation) {
	List<Token> returningTokens;
	returningTokens = new LinkedList<Token>();

	List<Annotation> tokenList = getSortedTokenList(document, annotation,
		false);

	for (int i = 0; i < tokenList.size(); i++) {
	    Annotation a = tokenList.get(i);
	    String kind = (String) a.getFeatures().get("kind");
	    if (!kind.equals("word"))
		continue;
	    String tokenValue = (String) a.getFeatures().get(
		    GateFormatConstants.TOKEN_FEATURE_NAME);
	    String stemValue = (String) a.getFeatures().get(
		    GateFormatConstants.STEM_FEATURE_NAME);
	    String pos = (String) a.getFeatures().get(
		    GateFormatConstants.POS_FEATURE_NAME);
	    Token token = new TokenImpl(tokenValue, pos, stemValue);
	    returningTokens.add(token);
	}
	return returningTokens;
    }

    @Override
    public String[] getPureTokensForAnnotation(Document document,
	    Annotation annotation, boolean useStemming) {
	List<Annotation> tokenList = getSortedTokenList(document, annotation,
		false);
	List<String> strTokens = new ArrayList<String>();
	for (Annotation a : tokenList) {
	    String kind = (String) a.getFeatures().get("kind");
	    if (!kind.equals("word"))
		continue;
	    String tokenFeatureName = useStemming ? GateFormatConstants.STEM_FEATURE_NAME
		    : GateFormatConstants.TOKEN_FEATURE_NAME;
	    String tokenValue = (String) a.getFeatures().get(tokenFeatureName);
	    strTokens.add(tokenValue);
	}
	return Iterables.toArray(strTokens, String.class);
    }

    @Override
    public String[] getTokensWithMathAnnotation(Document document,
	    Annotation annotation) {
	return extractTokensWithMathExpressions(document, annotation, null);
    }

    private String[] extractTokensWithMathExpressions(Document document,
	    Annotation annotation, String symbol) {
	List<Annotation> tokenList = getSortedTokenList(document, annotation,
		false);
	tokenList.addAll(getMathTokens(document, annotation));
	Collections.sort(tokenList, new OffsetComparator());
	List<String> strTokens = new ArrayList<String>();
	for (Annotation a : tokenList) {
	    String value = null;
	    if (a.getType().equals(TOKEN_ANNOTATION_NAME)) {
		value = (String) a.getFeatures().get(
			GateFormatConstants.TOKEN_FEATURE_NAME);
	    } else if (a.getType().equals(ARXMLIV_MATH_ANNOTATION_NAME)) {
		value = symbol == null ? String.format("$%s$", (String) a
			.getFeatures().get(ARXMLIV_MATH_TEX_ANNOTATION_NAME))
			: symbol;
	    }
	    if (value != null) {
		strTokens.add(value);
	    }
	}
	return Iterables.toArray(strTokens, String.class);
    }

    @Override
    public String[] getTokensWithTemplatedMathAnnotations(Document document,
	    Annotation annotation, char symbol) {
	return extractTokensWithMathExpressions(document, annotation,
		String.valueOf(symbol));
    }

    @Override
    public String getTextContentsForAnnotation(Document document,
	    Annotation annotation) {
	List<Annotation> tokenList = getSortedTokenList(document, annotation,
		true);
	StringBuffer sb = new StringBuffer();
	for (Annotation tokenAnnotation : tokenList) {
	    String tokenValue = (String) tokenAnnotation.getFeatures().get(
		    GateFormatConstants.TOKEN_FEATURE_NAME);
	    sb.append(tokenValue);
	}
	return sb.toString().trim();
    }

    @Override
    public String getTextContentsForAnnotationWithReplacements(
	    Document document, Annotation annotation,
	    Annotation annotationForReplace, final String replacementString) {
	List<Annotation> tokenList = getSortedTokenList(document, annotation,
		true);
	StringBuffer sb = new StringBuffer();
	for (Annotation tokenAnnotation : tokenList) {
	    if (tokenAnnotation.equals(annotationForReplace)) {
		sb.append(replacementString);
	    } else {
		String tokenValue = (String) tokenAnnotation.getFeatures().get(
			GateFormatConstants.TOKEN_FEATURE_NAME);
		sb.append(tokenValue);
	    }
	    sb.append(" ");
	}
	return sb.toString().trim();
    }

    @Override
    public List<Term> getTerms(String paperUrl, Document document,
	    Annotation annotation) {
	List<Term> terms = new ArrayList<Term>();
	AnnotationSet termSet = document
		.getAnnotations(ARXMLIV_MARKUP_NAME)
		.get(TERM_ANNOTATION_NAME)
		.getContained(annotation.getStartNode().getOffset(),
			annotation.getEndNode().getOffset());
	for (Annotation termAnnotation : termSet) {
	    String ontologyTermId = (String) termAnnotation.getFeatures().get(
		    "OMtermID");
	    if (ontologyTermId != null) {
		String confidenceScoreStr = (String) termAnnotation
			.getFeatures().get("OMtermVal");
		if (confidenceScoreStr != null) {
		    float confidenceScore = Float
			    .parseFloat(confidenceScoreStr);
		    String uri = format("%s/%d", paperUrl,
			    termAnnotation.getId());
		    String classUri = format("%s#%s", DOMAIN_ONTOLOGY_URI,
			    ontologyTermId);
		    String initialView = getTextContentsForAnnotation(document,
			    termAnnotation);
		    String normalizedView = (String) termAnnotation
			    .getFeatures().get(NORMALIZED_FORM_ANNOTATION_NAME);

		    List<MathExpression> mathExpressions = extractContainingMathExpressions(
			    document, termAnnotation);
		    Term term = new Term(termAnnotation.getId(), uri, classUri,
			    normalizedView, initialView, confidenceScore,
			    mathExpressions);
		    terms.add(term);
		} else {
		    logger.error(
			    "There's no any confidence score for a annotation = {} in a document = {}",
			    termAnnotation.getId(), document.getName());
		}
	    }
	}
	return terms;
    }

    private List<MathExpression> extractContainingMathExpressions(
	    Document document, Annotation termAnnotation) {
	List<MathExpression> mathExpressions = new ArrayList<MathExpression>();
	FeatureMap featureMap = Factory.newFeatureMap();
	featureMap.put("termid", termAnnotation.getId());
	AnnotationSet mathAnnotations = document.getAnnotations(
		ARXMLIV_MARKUP_NAME).get(ARXMLIV_MATH_ANNOTATION_NAME,
		featureMap);
	for (Annotation mathAnnotation : mathAnnotations) {
	    String latexExpression = (String) mathAnnotation.getFeatures().get(
		    ARXMLIV_MATH_TEX_ANNOTATION_NAME);
	    Integer varId = (Integer) mathAnnotation.getFeatures().get("varid");
	    if (varId != null) {
		mathExpressions.add(new Variable(mathAnnotation.getId(),
			latexExpression));
	    } else {
		List<Variable> variables = extractContainingVariables(document,
			mathAnnotation);
		mathExpressions.add(new Formula(mathAnnotation.getId(),
			latexExpression, variables));
	    }
	}
	return mathExpressions;
    }

    private List<Variable> extractContainingVariables(Document document,
	    Annotation mathAnnotation) {
	String varsAttribute = (String) mathAnnotation.getFeatures()
		.get("vars");
	List<Variable> variables = new ArrayList<Variable>();
	if (varsAttribute != null) {
	    String[] vars = varsAttribute.split(";");
	    FeatureMap varFeatureMap = Factory.newFeatureMap();
	    for (String var : vars) {
		varFeatureMap.put("varid", Integer.parseInt(var));
	    }
	    AnnotationSet containingVariableAnnotations = document
		    .getAnnotations(ARXMLIV_MARKUP_NAME).get(
			    ARXMLIV_MATH_ANNOTATION_NAME, varFeatureMap);
	    for (Annotation contVarAnnotation : containingVariableAnnotations) {
		String tex = (String) mathAnnotation.getFeatures().get(
			ARXMLIV_MATH_TEX_ANNOTATION_NAME);
		variables.add(new Variable(contVarAnnotation.getId(), tex));
	    }
	}
	return variables;
    }

    private List<Annotation> getSortedTokenList(Document document,
	    Annotation annotation, boolean withSpaces) {
	AnnotationSet tokenSet = getTokenSetWithoutSpaces(document, annotation);
	List<Annotation> tokenList = new ArrayList<Annotation>();
	for (Annotation token : tokenSet) {
	    AnnotationSet coveringMathAnnotations = document.getAnnotations(
		    ARXMLIV_MARKUP_NAME).getCovering(
		    ARXMLIV_MATH_ANNOTATION_NAME,
		    token.getStartNode().getOffset(),
		    token.getEndNode().getOffset());
	    if (coveringMathAnnotations.isEmpty()) {
		tokenList.add(token);
	    }
	}

	if (withSpaces) {
	    tokenList.addAll(getTokenSetWithSpaces(document, annotation));
	}

	Collections.sort(tokenList, new OffsetComparator());
	return tokenList;
    }

    private AnnotationSet getTokenSetWithoutSpaces(Document document,
	    Annotation annotation) {
	AnnotationSet tokenSet = document
		.getAnnotations(GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
		.get(TOKEN_ANNOTATION_NAME)
		.getContained(annotation.getStartNode().getOffset(),
			annotation.getEndNode().getOffset());
	return tokenSet;
    }

    private List<Annotation> getMathTokens(Document document,
	    Annotation annotation) {
	AnnotationSet mathTokens = document
		.getAnnotations(ARXMLIV_MARKUP_NAME)
		.get(ARXMLIV_MATH_ANNOTATION_NAME)
		.getContained(annotation.getStartNode().getOffset(),
			annotation.getEndNode().getOffset());
	return CollectionUtil.asList(mathTokens);
    }

    private AnnotationSet getTokenSetWithSpaces(Document document,
	    Annotation annotation) {

	AnnotationSet spaceTokenSet = document
		.getAnnotations(GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
		.get(SPACE_TOKEN_ANNOTATION_NAME)
		.getContained(annotation.getStartNode().getOffset(),
			annotation.getEndNode().getOffset());

	return spaceTokenSet;
    }
}
