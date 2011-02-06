package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.ui.actions.LabelRetargetAction;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.inject.Inject;

public class StructuralElementSearcherImpl implements StructuralElementSearcher {
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;
	@Inject
	private AnnotationUtil annotationUtil;

	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	private Set<String> nameSet = ArxmlivStructureElementTypes.toNameSet();

	private AnnotationSet structuralAnnotations;

	@Override
	public List<StructuralElement> retrieve(Document document) {

		setStructuralAnnotations(document
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.get(nameSet));

		Function<Annotation, StructuralElement> extractFunction = new ExtractionFunction(
				document);

		Iterable<StructuralElement> structuralElementIterable = Iterables
				.transform(structuralAnnotations, extractFunction);
		return CollectionUtil.asList(structuralElementIterable);
	}

	@Override
	public StructuralElement findById(Document document, int id) {
		Set<String> nameSet = ArxmlivStructureElementTypes.toNameSet();
		AnnotationSet structuralAnnotations = document
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.get(nameSet);
		Annotation foundAnnotation = null;
		for (Annotation annotation : structuralAnnotations) {
			if (annotation.getId().equals(id)) {
				foundAnnotation = annotation;
			}
		}
		if (foundAnnotation == null)
			throw new RuntimeException(
					String
							.format(
									"there is no structural element with id='%d' in document %s",
									id, document.getName()));
		StructuralElement foundElement = new ExtractionFunction(document)
				.apply(foundAnnotation);
		return foundElement;
	}

	@Override
	public StructuralElement findClosestPredecessor(Document document,
			final int id,
			final MocassinOntologyClasses... filterPredecessorTypes) {
		List<StructuralElement> elements = retrieve(document);

		Predicate<StructuralElement> typeFilter = new Predicate<StructuralElement>() {

			@Override
			public boolean apply(StructuralElement element) {
				MocassinOntologyClasses elementType = getStructuralElementTypeRecognizer()
						.predict(element);
				return Arrays.asList(filterPredecessorTypes).contains(
						elementType);
			}
		};

		List<StructuralElement> filteredElements = CollectionUtil
				.asList(Iterables.filter(elements, typeFilter));

		Predicate<StructuralElement> findById = new Predicate<StructuralElement>() {

			@Override
			public boolean apply(StructuralElement element) {
				return element.getId() == id;
			}
		};

		StructuralElement foundElement = Iterables.find(elements, findById);
		if (!filteredElements.contains(foundElement))
			filteredElements.add(foundElement);

		Collections.sort(filteredElements,
				new StructuralElementByLocationComparator());

		int foundElementIndex = filteredElements.indexOf(foundElement);

		int predecessorIndex = foundElementIndex - 1;

		return predecessorIndex > -1 ? filteredElements.get(predecessorIndex)
				: null;
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
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

	public List<Token> getTokensForAnnotation(Document document,
			Annotation annotation) {
		return getAnnotationUtil().getTokensForAnnotation(document, annotation,
				false);
	}

	public AnnotationSet getStructuralAnnotations() {
		return structuralAnnotations;
	}

	public void setStructuralAnnotations(AnnotationSet structuralAnnotations) {
		this.structuralAnnotations = structuralAnnotations;
	}

	private class ExtractionFunction implements
			Function<Annotation, StructuralElement> {
		private Document document;

		public ExtractionFunction(Document document) {
			this.document = document;
		}

		public Document getDocument() {
			return document;
		}

		@Override
		public StructuralElement apply(Annotation annotation) {
			Integer id = annotation.getId();
			Long start = annotation.getStartNode().getOffset();
			Long end = annotation.getEndNode().getOffset();
			String type = annotation.getType();
			String classFeature = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.CLASS_ATTRIBUTE_NAME);
			String name = classFeature != null ? classFeature : type;
			StructuralElement element = new StructuralElementImpl.Builder(id)
					.start(start).end(end).name(name).build();

			List<String> labels = collectLabels(annotation);
			element.setLabels(labels);
			AnnotationSet titleSet = getDocument()
					.getAnnotations(
							getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
					.get(
							getProperty(GateFormatConstants.TITLE_ANNOTATION_NAME_PROPERTY_KEY))
					.getContained(annotation.getStartNode().getOffset(),
							annotation.getEndNode().getOffset());
			List<Annotation> titleList = new ArrayList<Annotation>(titleSet);
			Collections.sort(titleList, new OffsetComparator());
			List<Token> titleTokens = null;
			if (titleList.size() > 0) {
				Annotation title = titleList.get(0);
				titleTokens = getTokensForAnnotation(getDocument(), title);
			}

			element.setTitleTokens(titleTokens != null ? titleTokens
					: new ArrayList<Token>());
			return element;
		}

		/**
		 * return all labels for a given annotation and for all contained
		 * non-arxmliv annotations
		 * 
		 * @param annotation
		 * @return
		 */
		private List<String> collectLabels(Annotation annotation) {
			List<String> labels = extractLabels(annotation);
			AnnotationSet containedElementSet = getDocument()
					.getAnnotations(
							getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
					.getContained(annotation.getStartNode().getOffset(),
							annotation.getEndNode().getOffset());
			ImmutableSet<Annotation> containedAxiliaryElements = Sets
					.difference(containedElementSet, getStructuralAnnotations())
					.immutableCopy();
			for (Annotation a : containedAxiliaryElements) {
				labels.addAll(extractLabels(a));
			}
			return labels;
		}

		private List<String> extractLabels(Annotation annotation) {
			String labelStr = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.LABEL_ATTRIBUTE_NAME);
			List<String> labels = StringUtil.tokenize(labelStr);
			return labels;
		}
	}
}
