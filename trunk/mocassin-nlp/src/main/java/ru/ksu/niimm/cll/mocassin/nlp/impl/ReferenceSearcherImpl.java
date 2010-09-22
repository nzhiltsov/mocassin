package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivFormatConstants;

public class ReferenceSearcherImpl implements ReferenceSearcher {
	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;
	@Inject
	private AnnotationUtil annotationUtil;

	private List<StructuralElement> structuralElements;

	@Override
	public List<Reference> retrieve(Document document) {
		loadStructuralElements(document);

		AnnotationSet refAnnotations = document
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.get(
						getProperty(GateFormatConstants.ARXMLIV_REF_ANNOTATION_PROPERTY_KEY));
		Function<Annotation, Reference> extractFunction = new ExtractFunction(
				document);
		Iterable<Reference> referenceIterable = Iterables.transform(
				refAnnotations, extractFunction);
		return CollectionUtil.asList(referenceIterable);
	}

	private void loadStructuralElements(Document document) {
		setStructuralElements(getStructuralElementSearcher().retrieve(document));
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

	public AnnotationUtil getAnnotationUtil() {
		return annotationUtil;
	}

	public List<String> getTokensForAnnotation(Document document,
			Annotation annotation) {
		return getAnnotationUtil().getTokensForAnnotation(document, annotation);
	}

	private class ExtractFunction implements Function<Annotation, Reference> {
		private Document document;

		public ExtractFunction(Document document) {
			this.document = document;
		}

		public Document getDocument() {
			return document;
		}

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

			Reference reference = new ReferenceImpl.Builder(id).from(from).to(
					to).document(documentName).build();
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
			if (sentenceSet.size() != 1) {
				throw new RuntimeException(
						String
								.format(
										"Enclosing sentence of the reference with id='%d' is not single",
										annotation.getId()));
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
				throw new RuntimeException(String.format(
						"parent element for ref with id='%d' not found",
						annotation.getId()));
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
