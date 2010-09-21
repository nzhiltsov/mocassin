package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivStructureElementTypes;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class StructuralElementSearcherImpl implements StructuralElementSearcher {
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;

	@Override
	public List<StructuralElement> retrieve(Document document) {
		Set<String> nameSet = ArxmlivStructureElementTypes.toNameSet();
		AnnotationSet structuralAnnotations = document
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.get(nameSet);

		Function<Annotation, StructuralElement> extractFunction = new ExtractionFunction(
				document);

		Iterable<StructuralElement> structuralElementIterable = Iterables
				.transform(structuralAnnotations, extractFunction);
		return CollectionUtil.asList(structuralElementIterable);
	}

	public String getProperty(String key) {
		return getNlpModulePropertiesLoader().get(key);
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
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
			String label = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.LABEL_ATTRIBUTE_NAME);
			element.setLabel(label);
			AnnotationSet titleSet = getDocument()
					.getAnnotations(
							getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
					.get(
							getProperty(GateFormatConstants.TITLE_ANNOTATION_NAME_PROPERTY_KEY))
					.getContained(annotation.getStartNode().getOffset(),
							annotation.getEndNode().getOffset());
			List<String> titleTokens = null;
			if (titleSet.size() > 0) {
				Annotation title = titleSet.iterator().next();
				titleTokens = getTokensFromTitle(title);
			}

			element.setTitleTokens(titleTokens);
			return element;
		}
		
		private List<String> getTokensFromTitle(Annotation title) {
			List<String> titleTokens;
			titleTokens = new LinkedList<String>();
			
			AnnotationSet tokenSet = getDocument()
					.getAnnotations(
							GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
					.get(
							getProperty(GateFormatConstants.TOKEN_ANNOTATION_NAME_PROPERTY_KEY))
					.getContained(title.getStartNode().getOffset(),
							title.getEndNode().getOffset());
			Iterator<Annotation> it = tokenSet.iterator();
			while (it.hasNext()) {
				Annotation a = it.next();
				String token = (String) a.getFeatures().get("string");
				titleTokens.add(token);
			}
			return titleTokens;
		}
	}
}
