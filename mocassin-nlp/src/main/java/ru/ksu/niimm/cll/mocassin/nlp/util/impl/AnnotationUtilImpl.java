package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.impl.TokenImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

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

	@Inject
	AnnotationUtilImpl(
			@Named("token.annotation.name") String tokenAnnotationName,
			@Named("arxmliv.math.annotation.name") String arxmlivMathAnnotationName,
			@Named("arxmliv.math.tex.annotation.name") String arxmlivMathTexAnnotationName,
			@Named("arxmliv.markup.name") String arxmlivMarkupName,
			@Named("space.token.annotation.name") String spaceTokenAnnotationName,
			@Named("sentence.annotation.name") String sentenceAnnotationName) {
		this.TOKEN_ANNOTATION_NAME = tokenAnnotationName;
		this.ARXMLIV_MATH_ANNOTATION_NAME = arxmlivMathAnnotationName;
		this.ARXMLIV_MATH_TEX_ANNOTATION_NAME = arxmlivMathTexAnnotationName;
		this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
		this.SPACE_TOKEN_ANNOTATION_NAME = spaceTokenAnnotationName;
		this.SENTENCE_ANNOTATION_NAME = sentenceAnnotationName;
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
			if (!coveringGroups.isEmpty() || xmlIdAttr != null && xmlIdAttr.contains(FREE_EXPRESSION_MARK)) {
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
