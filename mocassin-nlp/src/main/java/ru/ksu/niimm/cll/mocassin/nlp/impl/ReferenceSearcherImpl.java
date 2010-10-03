package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ReferenceSearcherImpl implements ReferenceSearcher {
	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;
	@Inject
	private AnnotationUtil annotationUtil;

	private List<StructuralElement> structuralElements;

	private Document document;

	@Override
	public List<Reference> retrieve(Document document) {
		setDocument(document);
		loadStructuralElements();

		AnnotationSet refAnnotations = document
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.get(
						getProperty(GateFormatConstants.ARXMLIV_REF_ANNOTATION_PROPERTY_KEY));
		Iterable<Annotation> filteredRefAnnotations = Iterables.filter(
				refAnnotations, new NotInMathPredicate(
						getNlpModulePropertiesLoader(), getDocument()));

		Iterable<Reference> referenceIterable = Iterables.transform(
				filteredRefAnnotations, new ExtractFunction());
		return CollectionUtil.asList(referenceIterable);
	}

	private void loadStructuralElements() {
		setStructuralElements(getStructuralElementSearcher().retrieve(
				getDocument()));
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	public String getProperty(String key) {
		return getNlpModulePropertiesLoader().get(key);
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	public Document getDocument() {
		return document;
	}

	private void setDocument(Document document) {
		this.document = document;
	}

	public AnnotationUtil getAnnotationUtil() {
		return annotationUtil;
	}

	public List<String> getTokensForAnnotation(Document document,
			Annotation annotation) {
		return getAnnotationUtil().getTokensForAnnotation(document, annotation);
	}

	/**
	 * Transformation from reference annotations to DAOs
	 * 
	 * @author nzhiltsov
	 * 
	 */
	private class ExtractFunction implements Function<Annotation, Reference> {

		@Override
		public Reference apply(Annotation annotation) {
			int id = annotation.getId();
			String labelref = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.LABEL_REF_ATTRIBUTE_NAME);
			StructuralElement to = getElementByLabel(labelref);
			StructuralElement from = getEnclosingElement(annotation);
			Annotation enclosingSentence = getEnclosingSentence(annotation);
			List<String> sentenceTokens = getTokensForAnnotation(getDocument(),
					enclosingSentence);
			String documentName = getDocument().getName();

			String additionalRefid = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.REF_ID_ATTRIBUTE_NAME);

			Reference reference = new ReferenceImpl.Builder(id).from(from).to(
					to).document(documentName).additionalRefid(additionalRefid)
					.build();
			reference.setSentenceTokens(sentenceTokens);
			return reference;
		}

		private Annotation getEnclosingSentence(Annotation annotation) {
			AnnotationSet sentenceSet = getDocument()
					.getAnnotations(
							GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
					.getCovering(
							getProperty(GateFormatConstants.SENTENCE_ANNOTATION_NAME_PROPERTY_KEY),
							annotation.getStartNode().getOffset(),
							annotation.getEndNode().getOffset());
			if (sentenceSet.size() == 0) {
				AnnotationSet allSentences = getDocument()
						.getAnnotations(
								GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
						.get(
								getProperty(GateFormatConstants.SENTENCE_ANNOTATION_NAME_PROPERTY_KEY));
				long distance = Long.MAX_VALUE;
				Annotation closestSentence = null;
				for (Annotation sentence : allSentences) {
					long endDistance = Math.abs(sentence.getEndNode()
							.getOffset()
							- annotation.getStartNode().getOffset());
					long startDistance = Math.abs(sentence.getStartNode()
							.getOffset()
							- annotation.getStartNode().getOffset());
					long minDistance = Math.min(endDistance, startDistance);
					if (minDistance < distance) {
						closestSentence = sentence;
						distance = minDistance;
					}
				}
				if (closestSentence == null) {
					throw new RuntimeException(
							String
									.format(
											"couldn't locate sentence for annotation with id='%s'",
											annotation.getId()));
				} else {
					return closestSentence;
				}
			}
			return sentenceSet.iterator().next();
		}

		private StructuralElement getEnclosingElement(Annotation annotation) {
			long refOffset = annotation.getStartNode().getOffset();
			StructuralElement closestParentElement = null;
			List<StructuralElement> elements = getStructuralElements();
			for (StructuralElement element : elements) {
				long start = element.getStart();
				long end = element.getEnd();
				boolean isEnclosing = refOffset >= start && refOffset <= end;
				if (!isEnclosing)
					continue;
				if (closestParentElement == null) {
					closestParentElement = element;
				} else if (refOffset - start < refOffset
						- closestParentElement.getStart()) {
					closestParentElement = element;
				}

			}

			if (closestParentElement == null) {
				throw new RuntimeException(
						String
								.format(
										"parent element for ref with id='%d' in document '%s' not found",
										annotation.getId(), getDocument()
												.getName()));
			}

			return closestParentElement;
		}

		private StructuralElement getElementByLabel(String labelref) {
			List<StructuralElement> elements = getStructuralElements();
			for (StructuralElement element : elements) {
				if (labelref.equals(element.getLabel())) {
					return element;
				}
			}
			throw new RuntimeException(String.format(
					"element with label '%s' not found", labelref));
		}

	}

	public List<StructuralElement> getStructuralElements() {
		return structuralElements;
	}

	public void setStructuralElements(List<StructuralElement> structuralElements) {
		this.structuralElements = structuralElements;
	}

}
