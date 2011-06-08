package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

import com.google.common.base.Predicate;

/**
 * Predicate which goal is to filter out all the references in math expressions
 * 
 * @author nzhiltsov
 * 
 */
public class NotInMathPredicate implements Predicate<Annotation> {
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;

	private Document document;

	public NotInMathPredicate(NlpModulePropertiesLoader loader,
			Document document) {
		this.nlpModulePropertiesLoader = loader;
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

	public String getProperty(String key) {
		return getNlpModulePropertiesLoader().get(key);
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	@Override
	public boolean apply(Annotation annotation) {

		AnnotationSet coveringMathAnnotations = getDocument()
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.getCovering(
						getProperty(GateFormatConstants.ARXMLIV_MATH_ANNOTATION_PROPERTY_KEY),
						annotation.getStartNode().getOffset(),
						annotation.getEndNode().getOffset());
		return coveringMathAnnotations.isEmpty();
	}
}
