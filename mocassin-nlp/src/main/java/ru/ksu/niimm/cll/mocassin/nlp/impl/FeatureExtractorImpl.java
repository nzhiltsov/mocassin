package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.persist.SerialDataStore;
import gate.util.GateException;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ru.ksu.niimm.cll.mocassin.nlp.Feature;
import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceContext;

public class FeatureExtractorImpl implements FeatureExtractor {
	// TODO: move to properties file
	private static final String STORAGE_DIR = "file:///WINDOWS/gate-serial-datastore";
	/**
	 * window size = count of tokens on both sides of the word excluding
	 * punctuation marks and numbers
	 */
	private static final int TOKEN_WINDOW_SIZE = 3;

	public FeatureExtractorImpl() throws GateException {
		System.setProperty("gate.home",
				"/WINDOWS/programs/gate-5.2.1-build3581-BIN");
		System
				.setProperty("gate.builtin.creole.dir",
						"file:///WINDOWS/programs/gate-5.2.1-build3581-BIN/src/gate/resources/creole");
		Gate.init();
	}

	@Override
	public List<ReferenceContext> getReferenceContextList() throws Exception {

		SerialDataStore dataStore = new SerialDataStore(STORAGE_DIR);
		dataStore.open();
		List documents = dataStore.getLrIds("gate.corpora.DocumentImpl");
		List<ReferenceContext> referenceContextList = new ArrayList<ReferenceContext>();
		for (Object documentLrId : documents) {

			FeatureMap features = Factory.newFeatureMap();
			features.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
			features.put(DataStore.LR_ID_FEATURE_NAME, documentLrId);
			Document document = (Document) Factory.createResource(
					"gate.corpora.DocumentImpl", features);
			AnnotationSet arxmlivTags = document
					.getAnnotations("Original markups");
			AnnotationSet defaultAnnotations = document.getAnnotations("");
			Annotation aroundTextAnnotation = getAroundTextAnnotation(document,
					arxmlivTags);
			Annotation refAnnotation = findRefAnnotation(document, arxmlivTags,
					aroundTextAnnotation);
			Annotation sentenceAnnotation = getSentenceAnnotation(document,
					defaultAnnotations, refAnnotation);
			Iterable<Annotation> withinWindowTokens = getTokensWithinWindow(
					refAnnotation, defaultAnnotations, sentenceAnnotation);
			// TODO: refactor to builder pattern
			ReferenceContext referenceContext = new ReferenceContextImpl();
			referenceContext.setRefid((String) aroundTextAnnotation
					.getFeatures().get("refid"));
			referenceContext.setFilename((String) aroundTextAnnotation
					.getFeatures().get("filename"));
			referenceContext.setFrom((String) aroundTextAnnotation
					.getFeatures().get("from"));
			referenceContext.setTo((String) aroundTextAnnotation.getFeatures()
					.get("to"));
			boolean isLeft = true;
			List<String> leftWordVector = new LinkedList<String>();
			List<String> rightWordVector = new LinkedList<String>();
			List<String> leftPosVector = new LinkedList<String>();
			List<String> rightPosVector = new LinkedList<String>();
			for (Annotation tokenAnnotation : withinWindowTokens) {
				if (tokenAnnotation.equals(refAnnotation)) {
					isLeft = false;
					continue;
				}
				String word = (String) tokenAnnotation.getFeatures().get(
						"string");
				String pos = (String) tokenAnnotation.getFeatures().get(
						"category");
				if (isLeft) {
					leftWordVector.add(word);
					leftPosVector.add(pos);
				} else {
					rightWordVector.add(word);
					rightPosVector.add(pos);
				}
			}
			Feature wordFeature = new WordFeature(new Vector<String>(
					leftWordVector), new Vector<String>(rightWordVector));
			Feature posFeature = new PosFeature(new Vector<String>(
					leftPosVector), new Vector<String>(rightPosVector));
			List<Feature> featuresList = new ArrayList<Feature>();
			featuresList.add(wordFeature);
			featuresList.add(posFeature);
			referenceContext.setFeatures(featuresList);
			referenceContextList.add(referenceContext);
		}
		dataStore.close();
		return referenceContextList;
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
	private Iterable<Annotation> getTokensWithinWindow(
			Annotation refAnnotation, AnnotationSet defaultAnnotations,
			Annotation sentenceAnnotation) {
		FeatureMap tokenFeatures = Factory.newFeatureMap();
		tokenFeatures.put("kind", "word");
		AnnotationSet tokens = defaultAnnotations.get("Token", tokenFeatures)
				.getContained(sentenceAnnotation.getStartNode().getOffset(),
						sentenceAnnotation.getEndNode().getOffset());
		List tokensList = new ArrayList(tokens);
		tokensList.add(refAnnotation);
		Collections.sort(tokensList, new OffsetComparator());
		int refIndex = tokensList.indexOf(refAnnotation);
		int leftBound = refIndex - TOKEN_WINDOW_SIZE;
		int rightBound = refIndex + TOKEN_WINDOW_SIZE;
		int fromIndex = leftBound > 0 ? leftBound : 0;
		int toIndex = rightBound < tokensList.size() - 1 ? rightBound
				: tokensList.size() - 1;
		return tokensList.subList(fromIndex, toIndex + 1);
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
		AnnotationSet sentences = defaultAnnotations.get("Sentence").get(
				refAnnotation.getStartNode().getOffset(),
				refAnnotation.getEndNode().getOffset());
		if (sentences.size() != 1)
			throw new RuntimeException(
					String
							.format(
									"The document % is in inconsistent state: multiple sentences contain the reference %s",
									document.getName(), refAnnotation
											.toString()));
		return sentences.iterator().next();
	}

	private Annotation findRefAnnotation(Document document,
			AnnotationSet arxmlivTags, Annotation aroundTextAnnotation) {

		String refid = (String) aroundTextAnnotation.getFeatures().get("refid");
		FeatureMap refidFeatures = Factory.newFeatureMap();
		refidFeatures.put("refid", refid);
		AnnotationSet refs = arxmlivTags.get("ref", refidFeatures);
		if (refs.size() != 1)
			throw new RuntimeException(
					"The document % is in inconsistent state: multiple 'ref' annotations with identical 'refid' attribute value");
		Annotation refAnnotation = refs.iterator().next();

		return refAnnotation;
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
		AnnotationSet aroundText = arxmlivTags.get("aroundText");
		if (aroundText.size() != 1)
			throw new RuntimeException(
					String
							.format(
									"The document %s is in inconsistent state: multiple 'aroundText' annotations",
									document.getName()));
		Annotation aroundTextAnnotation = aroundText.iterator().next();
		return aroundTextAnnotation;
	}
}
