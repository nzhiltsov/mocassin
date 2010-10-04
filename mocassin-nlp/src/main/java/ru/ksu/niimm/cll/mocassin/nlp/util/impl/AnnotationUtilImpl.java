package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.impl.NotInMathPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class AnnotationUtilImpl implements AnnotationUtil {
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.cll.mocassin.nlp.util.impl.AnnotationUtil#getTokensForAnnotation
	 * (gate.Document, gate.Annotation)
	 */
	public List<String> getTokensForAnnotation(Document document,
			Annotation annotation, boolean useStemming) {
		List<String> titleTokens;
		titleTokens = new LinkedList<String>();

		AnnotationSet tokenSet = document
				.getAnnotations(GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
				.get(
						getProperty(GateFormatConstants.TOKEN_ANNOTATION_NAME_PROPERTY_KEY))
				.getContained(annotation.getStartNode().getOffset(),
						annotation.getEndNode().getOffset());
		Iterable<Annotation> filteredTokens = Iterables
				.filter(tokenSet, new NotInMathPredicate(
						getNlpModulePropertiesLoader(), document));

		List<Annotation> tokenList = CollectionUtil.asList(filteredTokens);

		Collections.sort(tokenList, new OffsetComparator());
		for (int i = 0; i < tokenList.size(); i++) {
			Annotation a = tokenList.get(i);
			String kind = (String) a.getFeatures().get("kind");
			if (!kind.equals("word"))
				continue;
			String tokenFeatureName = useStemming ? GateFormatConstants.STEM_FEATURE_NAME
					: GateFormatConstants.TOKEN_FEATURE_NAME;
			String token = (String) a.getFeatures().get(tokenFeatureName);
			titleTokens.add(token);
		}
		return titleTokens;
	}

	public String getProperty(String key) {
		return getNlpModulePropertiesLoader().get(key);
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}
}
