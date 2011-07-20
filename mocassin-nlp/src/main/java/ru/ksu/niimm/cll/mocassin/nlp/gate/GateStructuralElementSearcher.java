package ru.ksu.niimm.cll.mocassin.nlp.gate;

import edu.uci.ics.jung.graph.Graph;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementByLocationComparator;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.IdPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.TypeFilterPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.PdfReferenceEntry;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class GateStructuralElementSearcher implements StructuralElementSearcher {
	@Inject
	private Logger logger;
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;
	@Inject
	private AnnotationUtil annotationUtil;
	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private LatexDocumentDAO latexDocumentDAO;

	private static final Set<String> NAME_SET = ArxmlivStructureElementTypes
			.toNameSet();

	private AnnotationSet structuralAnnotations;

	private ParsedDocument parsedDocument;

	private LatexDocumentModel latexDocumentModel;

	@Override
	public List<StructuralElement> retrieveElements(
			ParsedDocument parsedDocument) {
		this.parsedDocument = parsedDocument;
		Document gateDocument = null;
		String arxivId = parsedDocument.getArxivId().replace("/", "_");
		try {
			gateDocument = gateDocumentDAO.load(arxivId);
			latexDocumentModel = latexDocumentDAO.load(parsedDocument
					.getArxivId());

			setStructuralAnnotations(gateDocument
					.getAnnotations(
							getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
					.get(NAME_SET));

			Function<Annotation, StructuralElement> extractFunction = new ExtractionFunction(
					gateDocument);

			Iterable<StructuralElement> structuralElementIterable = Iterables
					.transform(structuralAnnotations, extractFunction);
			return CollectionUtil.asList(structuralElementIterable);
		} catch (AccessGateDocumentException e) {
			logger.log(Level.SEVERE, String.format(
					"failed to load the document: %s", parsedDocument.getUri()));
			throw new RuntimeException(e);
		} catch (AccessGateStorageException e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"failed to access the storage while loading the document: %s",
							arxivId));
			throw new RuntimeException(e);
		} finally {
			gateDocumentDAO.release(gateDocument);
		}
	}

	@Override
	public StructuralElement findById(ParsedDocument parsedDocument, int id) {
		List<StructuralElement> elements = retrieveElements(parsedDocument);
		return Iterables.find(elements, new IdPredicate(id), null);
	}

	@Override
	public StructuralElement findClosestPredecessor(StructuralElement element,
			MocassinOntologyClasses[] validDomains,
			Graph<StructuralElement, Reference> graph) {
		Collection<StructuralElement> elements = graph.getVertices();
		List<StructuralElement> filteredElements = CollectionUtil
				.asList(Iterables.filter(elements, new TypeFilterPredicate(
						validDomains)));
		if (!filteredElements.contains(element))
			filteredElements.add(element);
		return getClosestPredecessor(filteredElements, element);
	}

	@Override
	public StructuralElement findClosestPredecessor(ParsedDocument document,
			final int id,
			final MocassinOntologyClasses... filterPredecessorTypes) {
		List<StructuralElement> elements = retrieveElements(document);

		List<StructuralElement> filteredElements = CollectionUtil
				.asList(Iterables.filter(elements, new TypeFilterPredicate(
						filterPredecessorTypes)));

		Predicate<StructuralElement> findById = new Predicate<StructuralElement>() {

			@Override
			public boolean apply(StructuralElement element) {
				return element.getId() == id;
			}
		};

		StructuralElement foundElement = Iterables.find(elements, findById);
		if (!filteredElements.contains(foundElement))
			filteredElements.add(foundElement);
		return getClosestPredecessor(filteredElements, foundElement);
	}

	private StructuralElement getClosestPredecessor(
			List<StructuralElement> elements, StructuralElement successorElement) {
		Collections.sort(elements, new StructuralElementByLocationComparator());

		int foundElementIndex = elements.indexOf(successorElement);

		int predecessorIndex = foundElementIndex - 1;

		return predecessorIndex > -1 ? elements.get(predecessorIndex) : null;
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

	private ParsedDocument getParsedDocument() {
		return parsedDocument;
	}

	private String getTextContentsForAnnotation(Document document,
			Annotation annotation) {
		return getAnnotationUtil().getTextContentsForAnnotation(document,
				annotation);
	}

	public String getPdfUri() {
		return parsedDocument.getPdfUri();
	}

	public String[] getPureTokensForAnnotation(Document document,
			Annotation annotation) {
		return annotationUtil.getPureTokensForAnnotation(document, annotation,
				false);
	}

	public AnnotationSet getStructuralAnnotations() {
		return structuralAnnotations;
	}

	public void setStructuralAnnotations(AnnotationSet structuralAnnotations) {
		this.structuralAnnotations = structuralAnnotations;
	}

	private void fillPageNumber(StructuralElement element) {
		if (element.getLabels().isEmpty())
			return;
		List<PdfReferenceEntry> labels = latexDocumentModel.getLabels();
		for (PdfReferenceEntry entry : labels) {// labels are ordered by their
												// positions
			String labelText = String.format("LABEL:%s", entry.key());
			if (element.getLabels().contains(labelText)) {
				element.setStartPageNumber(entry.getPdfNumberPage());
				break;
			}
		}
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

			List<String> labels = collectLabels(annotation);

			AnnotationSet titleSet = getDocument()
					.getAnnotations(
							getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
					.get(getProperty(GateFormatConstants.TITLE_ANNOTATION_NAME_PROPERTY_KEY))

					.getContained(annotation.getStartNode().getOffset(),
							annotation.getEndNode().getOffset());
			List<Annotation> titleList = new ArrayList<Annotation>(titleSet);
			Collections.sort(titleList, new OffsetComparator());
			String title = "";
			if (titleList.size() > 0) {
				Annotation titleAnnotation = titleList.get(0);
				title = getTextContentsForAnnotation(getDocument(),
						titleAnnotation);
			} else {
				String refnum = (String) annotation.getFeatures().get(
						ArxmlivFormatConstants.REF_NUM_ATTRIBUTE_NAME);
				title = refnum != null ? String.format("%s %s", name, refnum)
						: name;
			}

			StructuralElement element = new StructuralElementImpl.Builder(id,
					getParsedDocument().getUri() + "/" + id).start(start)
					.end(end).name(name).title(title).build();
			element.setLabels(labels);
			element.setContents(getPureTokensForAnnotation(getDocument(),
					annotation));
			fillPageNumber(element);

			MocassinOntologyClasses predictedClass = getStructuralElementTypeRecognizer()
					.predict(element);
			element.setPredictedClass(predictedClass);

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
