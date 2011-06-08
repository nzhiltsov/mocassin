package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.impl.NotInMathPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.impl.TokenImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class AnnotationUtilImpl implements AnnotationUtil {

	private final NlpModulePropertiesLoader nlpModulePropertiesLoader;

	@Inject
	private AnnotationUtilImpl(
			NlpModulePropertiesLoader nlpModulePropertiesLoader) {
		this.nlpModulePropertiesLoader = nlpModulePropertiesLoader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.cll.mocassin.nlp.util.impl.AnnotationUtil#getTokensForAnnotation
	 * (gate.Document, gate.Annotation)
	 */
	public List<Token> getTokensForAnnotation(Document document,
			Annotation annotation, boolean useStemming) {
		List<Token> returningTokens;
		returningTokens = new LinkedList<Token>();

		List<Annotation> tokenList = getSortedTokenList(document, annotation,
				false);

		for (int i = 0; i < tokenList.size(); i++) {
			Annotation a = tokenList.get(i);
			String kind = (String) a.getFeatures().get("kind");
			if (!kind.equals("word"))
				continue;
			String tokenFeatureName = useStemming ? GateFormatConstants.STEM_FEATURE_NAME
					: GateFormatConstants.TOKEN_FEATURE_NAME;
			String tokenValue = (String) a.getFeatures().get(tokenFeatureName);
			String pos = (String) a.getFeatures().get(
					GateFormatConstants.POS_FEATURE_NAME);
			Token token = new TokenImpl(tokenValue, pos);
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

	private String getProperty(String key) {
		return getNlpModulePropertiesLoader().get(key);
	}

	private NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	private List<Annotation> getSortedTokenList(Document document,
			Annotation annotation, boolean withSpaces) {
		AnnotationSet tokenSet = getTokenSetWithoutSpaces(document, annotation);
		Iterable<Annotation> filteredTokens = Iterables
				.filter(tokenSet, new NotInMathPredicate(
						getNlpModulePropertiesLoader(), document));

		List<Annotation> tokenList = CollectionUtil.asList(filteredTokens);

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
				.get(
						getProperty(GateFormatConstants.TOKEN_ANNOTATION_NAME_PROPERTY_KEY))
				.getContained(annotation.getStartNode().getOffset(),
						annotation.getEndNode().getOffset());
		return tokenSet;
	}

	private AnnotationSet getTokenSetWithSpaces(Document document,
			Annotation annotation) {

		AnnotationSet spaceTokenSet = document
				.getAnnotations(GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
				.get(
						getProperty(GateFormatConstants.SPACE_TOKEN_ANNOTATION_NAME_PROPERTY_KEY))
				.getContained(annotation.getStartNode().getOffset(),
						annotation.getEndNode().getOffset());

		return spaceTokenSet;
	}
}
