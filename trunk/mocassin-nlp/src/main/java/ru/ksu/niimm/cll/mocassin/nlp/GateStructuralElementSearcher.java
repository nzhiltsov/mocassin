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

import org.slf4j.Logger;

import static ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl.NodePositionComparator;
import static ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.DescPositionComparator;

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
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator.SimilarityMetrics;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

class GateStructuralElementSearcher implements StructuralElementSearcher {
	private final String ARXMLIV_MARKUP_NAME;
	private final String TITLE_ANNOTATION_NAME;
	@InjectLogger
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

	private AnnotationSet structuralAnnotations;

	private ParsedDocument parsedDocument;

	private LatexDocumentModel latexDocumentModel;
	private List<Node> latexNodes;

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
		String arxivId = parsedDocument.getCollectionId().replace("/", "_");
		try {
			gateDocument = gateDocumentDAO.load(arxivId);
			latexDocumentModel = latexDocumentDAO.load(parsedDocument
					.getCollectionId());
			latexNodes = new ArrayList<Node>(structureBuilder
					.buildStructureGraph(latexDocumentModel).getVertices());
			Collections.sort(latexNodes, new NodePositionComparator());

			setStructuralAnnotations(gateDocument);

			List<StructuralElement> elements = new ArrayList<StructuralElement>();
			for (Annotation structuralAnnotation : getStructuralAnnotations()) {
				StructuralElement element = extractStructuralElement(
						gateDocument, structuralAnnotation);
				elements.add(element);
			}

			fillPageNumbers(elements);
			return elements;
		} catch (AccessGateDocumentException e) {
			logger.error("Failed to load the document: {}",
					parsedDocument.getUri(), e);
			throw new RuntimeException(e);
		} catch (AccessGateStorageException e) {
			logger.error(
					"Failed to access the storage while loading the document: {}",
					arxivId, e);
			throw new RuntimeException(e);
		} finally {
			gateDocumentDAO.release(gateDocument);
		}
	}

	private void fillPageNumbers(List<StructuralElement> elements) {
		Collections.sort(elements, new StructuralElementByLocationComparator());
		int j = 0;
		for (StructuralElement element : elements) {
			if (element.getPredictedClass() == MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT)
				continue;
			String[] elementLabels = element.getPredictedClass().getLabels();
			while (j < latexNodes.size()) {
				Node node = latexNodes.get(j);
				boolean found = false;
				for (String label : elementLabels) {
					float similarity = StringSimilarityEvaluator.getSimilarity(
							node.getName(), label, SimilarityMetrics.N_GRAM);
					if (similarity >= 0.8) {
						fillElementLocation(element, node);
						found = true;
						break;
					}
				}
				j++;
				if (found) {
					break;
				}
			}
			if (element.getStartPageNumber() == 0) {
				logger.info("Couldn't determine location of an element='{}'",
						element.getUri());
			}
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

	public void setStructuralAnnotations(Document gateDocument) {
		this.structuralAnnotations = getAnnotationUtil()
				.getStructuralAnnotations(gateDocument);
	}

	private void fillElementLocation(StructuralElement element, Node node) {
		element.setStartPageNumber(node.getPdfPageNumber());
		element.setLatexStartLine(node.getBeginLine());
		element.setLatexEndLine(node.getEndLine());
	}

	private StructuralElement extractStructuralElement(Document document,
			Annotation annotation) {
		Integer id = annotation.getId();
		Long start = annotation.getStartNode().getOffset();
		Long end = annotation.getEndNode().getOffset();
		String type = annotation.getType();
		String classFeature = (String) annotation.getFeatures().get(
				ArxmlivFormatConstants.CLASS_ATTRIBUTE_NAME);

		String name = classFeature != null
				&& !type.equals(ArxmlivStructureElementTypes.TABLE.toString()) ? classFeature
				: type;

		List<String> labels = collectLabels(document, annotation);

		AnnotationSet titleSet = document
				.getAnnotations(ARXMLIV_MARKUP_NAME)
				.get(TITLE_ANNOTATION_NAME)
				.getContained(annotation.getStartNode().getOffset(),
						annotation.getEndNode().getOffset());
		List<Annotation> titleList = new ArrayList<Annotation>(titleSet);
		Collections.sort(titleList, new OffsetComparator());
		String title = "";
		if (id != 0) {
			if (titleList.size() > 0) {
				Annotation titleAnnotation = titleList.get(0);
				title = getTextContentsForAnnotation(document, titleAnnotation);
			} else {
				String refnum = (String) annotation.getFeatures().get(
						ArxmlivFormatConstants.REF_NUM_ATTRIBUTE_NAME);
				title = refnum != null ? String.format("%s %s", name, refnum)
						: name;
			}
		}

		StructuralElement element = new StructuralElementImpl.Builder(id,
				getParsedDocument().getUri() + "/" + id).start(start).end(end)
				.name(name).title(title).build();
		element.setLabels(labels);
		if (id != 0) {
			element.setContents(getPureTokensForAnnotation(document, annotation));
		}
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
	private List<String> collectLabels(Document document, Annotation annotation) {
		List<String> labels = extractLabels(annotation);
		AnnotationSet containedElementSet = document.getAnnotations(
				ARXMLIV_MARKUP_NAME).getContained(
				annotation.getStartNode().getOffset(),
				annotation.getEndNode().getOffset());
		ImmutableSet<Annotation> containedAxiliaryElements = Sets.difference(
				containedElementSet, getStructuralAnnotations())
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
