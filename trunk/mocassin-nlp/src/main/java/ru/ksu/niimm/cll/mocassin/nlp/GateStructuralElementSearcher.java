package ru.ksu.niimm.cll.mocassin.nlp;

import edu.uci.ics.jung.graph.Graph;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementByLocationComparator;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.IdPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.TypeFilterPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

class GateStructuralElementSearcher implements StructuralElementSearcher {
	private final String ARXMLIV_MARKUP_NAME;
	private final String TITLE_ANNOTATION_NAME;
	@Inject
	private Logger logger;
	@Inject
	private AnnotationUtil annotationUtil;
	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private LatexDocumentDAO latexDocumentDAO;
	@Inject
	private StructureBuilder structureBuilder;

	private static final Set<String> NAME_SET = ArxmlivStructureElementTypes
			.toNameSet();

	private AnnotationSet structuralAnnotations;

	private ParsedDocument parsedDocument;

	private LatexDocumentModel latexDocumentModel;
	private Collection<Node> latexNodes;

	@Inject
	GateStructuralElementSearcher(
			@Named("arxmliv.markup.name") String arxmlivMarkupName,
			@Named("title.annotation.name") String titleAnnotationName) {
		this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
		this.TITLE_ANNOTATION_NAME = titleAnnotationName;
	}

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
			latexNodes = structureBuilder.buildStructureGraph(
					latexDocumentModel).getVertices();

			setStructuralAnnotations(gateDocument.getAnnotations(
					ARXMLIV_MARKUP_NAME).get(NAME_SET));

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
		for (Node node : latexNodes) {
			if (!element.getLabels().isEmpty()) {
				String labelText = String.format("LABEL:%s",
						node.getLabelText());
				if (element.getLabels().contains(labelText)) {
					fillElementLocation(element, node);
					break;
				}
			} else {
				/**
				 * TODO: this is a temporary workaround; use arxmliv coordinates
				 * when they will be available
				 */
				if (element.getPredictedClass() != MocassinOntologyClasses.SECTION)
					continue;
				else if (Arrays.asList(
						MocassinOntologyClasses.SECTION.getLabels()).contains(
						node.getName())
						&& element.getTitle().contains(node.getTitle())) {
					fillElementLocation(element, node);
					break;
				}
			}
		}

	}

	private void fillElementLocation(StructuralElement element, Node node) {
		element.setStartPageNumber(node.getPdfPageNumber());
		element.setLatexStartLine(node.getBeginLine());
		element.setLatexEndLine(node.getEndLine());
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

			String name = classFeature != null
					&& !type.equals(ArxmlivStructureElementTypes.TABLE
							.toString()) ? classFeature : type;

			List<String> labels = collectLabels(annotation);

			AnnotationSet titleSet = getDocument()
					.getAnnotations(ARXMLIV_MARKUP_NAME)
					.get(TITLE_ANNOTATION_NAME)

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

			MocassinOntologyClasses predictedClass = getStructuralElementTypeRecognizer()
					.predict(element);
			element.setPredictedClass(predictedClass);

			fillPageNumber(element);

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
			AnnotationSet containedElementSet = getDocument().getAnnotations(
					ARXMLIV_MARKUP_NAME).getContained(
					annotation.getStartNode().getOffset(),
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
