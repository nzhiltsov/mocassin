package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.util.OffsetComparator;
import ru.ksu.niimm.cll.mocassin.nlp.AnnotationAnalyzer;
import ru.ksu.niimm.cll.mocassin.nlp.Feature;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceContext;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

public class AnnotationAnalyzerImpl implements AnnotationAnalyzer {
	private static final String TOKEN_ANNOTATION_NAME_PROPERTY_KEY = "token.annotation.name";
	private static final String SENTENCE_ANNOTATION_NAME_PROPERTY_KEY = "sentence.annotation.name";
	private static final String REF_FEATURE_NAME_PROPERTY_KEY = "ref.feature.name";
	private static final String AROUND_TEXT_FEATURE_NAME_PROPERTY_KEY = "around.text.feature.name";
	private static final String TO_FEATURE_NAME_PROPERTY_KEY = "to.feature.name";
	private static final String FROM_FEATURE_NAME_PROPERTY_KEY = "from.feature.name";
	private static final String FILENAME_FEATURE_NAME_PROPERTY_KEY = "filename.feature.name";
	private static final String REFID_FEATURE_NAME_PROPERTY_KEY = "refid.feature.name";
	private static final String ARXMLIV_MARKUP_NAME_PROPERTY_KEY = "arxmliv.markup.name";

	private static final String DEFAULT_ANNOTATION_SET_NAME = "";
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;

	private int tokenWindowSize;

	@Override
	public ReferenceContext retrieveReferenceContext(Document document) {
		initTokenWindowSize();
		AnnotationSet arxmlivTags = document
				.getAnnotations(getProperty(ARXMLIV_MARKUP_NAME_PROPERTY_KEY));
		AnnotationSet defaultAnnotations = document
				.getAnnotations(DEFAULT_ANNOTATION_SET_NAME);
		Annotation aroundTextAnnotation = getAroundTextAnnotation(document,
				arxmlivTags);
		Annotation refAnnotation = findRefAnnotation(document, arxmlivTags,
				aroundTextAnnotation);
		Annotation sentenceAnnotation = getSentenceAnnotation(document,
				defaultAnnotations, refAnnotation);
		List<Annotation> withinWindowTokens = getTokensWithinWindow(
				refAnnotation, defaultAnnotations, sentenceAnnotation);

		ReferenceContext referenceContext = makeReferenceContext(aroundTextAnnotation);

		List<Feature> featuresList = convertToFeatures(refAnnotation,
				withinWindowTokens);
		referenceContext.setFeatures(featuresList);
		return referenceContext;
	}

	public void initTokenWindowSize() {
		this.tokenWindowSize = getNlpModulePropertiesLoader()
				.getWindowTokenSize();
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	/**
	 * search 'aroundText' annotation within given original markup set (arxmliv
	 * tags)
	 * 
	 * @param document
	 * @param arxmlivTags
	 * @return
	 */
	private Annotation getAroundTextAnnotation(Document document,
			AnnotationSet arxmlivTags) {
		AnnotationSet aroundText = arxmlivTags
				.get(getProperty(AROUND_TEXT_FEATURE_NAME_PROPERTY_KEY));
		if (aroundText.size() != 1)
			throw new RuntimeException(
					String
							.format(
									"The document %s is in inconsistent state: multiple 'aroundText' annotations",
									document.getName()));
		Annotation aroundTextAnnotation = aroundText.iterator().next();
		return aroundTextAnnotation;
	}

	private Annotation findRefAnnotation(Document document,
			AnnotationSet arxmlivTags, Annotation aroundTextAnnotation) {
		String refidName = getProperty(REFID_FEATURE_NAME_PROPERTY_KEY);
		String refid = (String) aroundTextAnnotation.getFeatures().get(
				refidName);
		FeatureMap refidFeatures = Factory.newFeatureMap();
		refidFeatures.put(refidName, refid);
		AnnotationSet refs = arxmlivTags.get(
				getProperty(REF_FEATURE_NAME_PROPERTY_KEY), refidFeatures);
		if (refs.size() != 1)
			throw new RuntimeException(
					"The document % is in inconsistent state: multiple 'ref' annotations with identical 'refid' attribute value");
		Annotation refAnnotation = refs.iterator().next();

		return refAnnotation;
	}

	/**
	 * return annotation of a sentence that contains given reference annotation
	 * 
	 * @param document
	 * @param refAnnotation
	 * @return
	 */
	private Annotation getSentenceAnnotation(Document document,
			AnnotationSet defaultAnnotations, Annotation refAnnotation) {
		AnnotationSet sentences = defaultAnnotations.get(
				getProperty(SENTENCE_ANNOTATION_NAME_PROPERTY_KEY)).get(
				refAnnotation.getStartNode().getOffset() - 1,
				refAnnotation.getEndNode().getOffset() - 1);
		if (sentences.size() != 1) {
			throw new RuntimeException(
					String
							.format(
									"The document %s is in inconsistent state: multiple sentences contain the reference %s",
									document.getName(), refAnnotation
											.toString()));
		}
		return sentences.iterator().next();
	}

	/**
	 * return set of tokens as a context of given reference according to
	 * pre-defined window size
	 * 
	 * @param document
	 * @param defaultAnnotations
	 * @param sentenceAnnotation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Annotation> getTokensWithinWindow(Annotation refAnnotation,
			AnnotationSet defaultAnnotations, Annotation sentenceAnnotation) {
		FeatureMap tokenFeatures = Factory.newFeatureMap();
		tokenFeatures.put("kind", "word");
		AnnotationSet tokens = defaultAnnotations.get(
				getProperty(TOKEN_ANNOTATION_NAME_PROPERTY_KEY), tokenFeatures)
				.getContained(sentenceAnnotation.getStartNode().getOffset(),
						sentenceAnnotation.getEndNode().getOffset());
		List tokensList = new ArrayList(tokens);
		tokensList.add(refAnnotation);
		Collections.sort(tokensList, new OffsetComparator());
		int refIndex = tokensList.indexOf(refAnnotation);
		int leftBound = refIndex - tokenWindowSize;
		int rightBound = refIndex + tokenWindowSize;
		int fromIndex = leftBound > 0 ? leftBound : 0;
		int toIndex = rightBound < tokensList.size() - 1 ? rightBound
				: tokensList.size() - 1;
		return tokensList.subList(fromIndex, toIndex + 1);
	}

	public String getProperty(String key) {
		return getNlpModulePropertiesLoader().get(key);
	}

	private ReferenceContext makeReferenceContext(
			Annotation aroundTextAnnotation) {
		ReferenceContext referenceContext = new ReferenceContextImpl();
		referenceContext.setRefid((String) aroundTextAnnotation.getFeatures()
				.get(getProperty(REFID_FEATURE_NAME_PROPERTY_KEY)));
		referenceContext.setFilename((String) aroundTextAnnotation
				.getFeatures().get(
						getProperty(FILENAME_FEATURE_NAME_PROPERTY_KEY)));
		referenceContext.setFrom((String) aroundTextAnnotation.getFeatures()
				.get(getProperty(FROM_FEATURE_NAME_PROPERTY_KEY)));
		referenceContext.setTo((String) aroundTextAnnotation.getFeatures().get(
				getProperty(TO_FEATURE_NAME_PROPERTY_KEY)));
		return referenceContext;
	}

	private String[] makeEmptyStringArray(int length) {
		String[] a = new String[length];
		Arrays.fill(a, "");
		return a;
	}

	/**
	 * convert given annotations to features
	 * 
	 * @param refAnnotation
	 * @param withinWindowTokens
	 * @return
	 */
	private List<Feature> convertToFeatures(Annotation refAnnotation,
			List<Annotation> withinWindowTokens) {
		String[] leftWordVector = makeEmptyStringArray(tokenWindowSize);
		String[] rightWordVector = makeEmptyStringArray(tokenWindowSize);
		String[] leftPosVector = makeEmptyStringArray(tokenWindowSize);
		String[] rightPosVector = makeEmptyStringArray(tokenWindowSize);

		int refIndex = withinWindowTokens.indexOf(refAnnotation);
		for (int l = 1; refIndex - l >= 0; l++) {
			Annotation next = withinWindowTokens.get(refIndex - l);
			String word = (String) next.getFeatures().get("string");
			String pos = (String) next.getFeatures().get("category");
			leftWordVector[tokenWindowSize - l] = word;
			leftPosVector[tokenWindowSize - l] = pos;
		}
		for (int r = 1; refIndex + r < withinWindowTokens.size(); r++) {
			Annotation next = withinWindowTokens.get(refIndex + r);
			String word = (String) next.getFeatures().get("string");
			String pos = (String) next.getFeatures().get("category");
			rightWordVector[r - 1] = word;
			rightPosVector[r - 1] = pos;
		}

		Feature wordFeature = new WordFeature(leftWordVector, rightWordVector);
		Feature posFeature = new PosFeature(leftPosVector, rightPosVector);
		List<Feature> featuresList = new ArrayList<Feature>();
		featuresList.add(wordFeature);
		featuresList.add(posFeature);
		return featuresList;
	}
}
