package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.classifier.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.classifier.Prediction;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import static ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElementImpl.DescPositionComparator;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

public class GateReferenceSearcher implements ReferenceSearcher {
	private final String ARXMLIV_MARKUP_NAME;
	private final String ARXMLIV_REF_ANNOTATION_NAME;
	private final String ARXMLIV_MATH_ANNOTATION_NAME;

	private final boolean USE_STEMMING;
	@InjectLogger
	private Logger logger;
	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	@Inject
	private AnnotationUtil annotationUtil;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	@Inject
	protected NavigationalRelationClassifier navigationalRelationClassifier;
	@Inject
	protected ProvesRelationAnalyzer provesRelationAnalyzer;
	@Inject
	protected HasConsequenceRelationAnalyzer hasConsequenceRelationAnalyzer;
	@Inject
	protected ExemplifiesRelationAnalyzer exemplifiesRelationAnalyzer;

	private List<StructuralElement> structuralElements;

	private Graph<StructuralElement, Reference> graph;

	private Document document;

	private ParsedDocument parsedDocument;

	@Inject
	GateReferenceSearcher(
			@Named("arxmliv.markup.name") String arxmlivMarkupName,
			@Named("arxmliv.ref.annotation.name") String arxmlivRefAnnotationName,
			@Named("arxmliv.math.annotation.name") String arxmlivMathAnnotationName,
			@Named("useStemming") String useStemming) {
		this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
		this.ARXMLIV_REF_ANNOTATION_NAME = arxmlivRefAnnotationName;
		this.ARXMLIV_MATH_ANNOTATION_NAME = arxmlivMathAnnotationName;

		this.USE_STEMMING = Boolean.parseBoolean(useStemming);
	}

	@Override
	public Graph<StructuralElement, Reference> retrieveStructuralGraph(
			ParsedDocument parsedDocument) {
		this.parsedDocument = parsedDocument;
		String arxivId = StringUtil.arxivid2gateid(parsedDocument
				.getCollectionId());
		try {
			this.graph = new DirectedSparseMultigraph<StructuralElement, Reference>();

			setDocument(gateDocumentDAO.load(arxivId));

			loadStructuralElements(parsedDocument);

			addPartholeRelations();

			AnnotationSet refAnnotations = getDocument().getAnnotations(
					ARXMLIV_MARKUP_NAME).get(ARXMLIV_REF_ANNOTATION_NAME);
			List<Annotation> filteredRefAnnotations = new ArrayList<Annotation>();
			for (Annotation refAnnotation : refAnnotations) {
				AnnotationSet coveringMathAnnotations = getDocument()
						.getAnnotations(ARXMLIV_MARKUP_NAME).getCovering(
								ARXMLIV_MATH_ANNOTATION_NAME,
								refAnnotation.getStartNode().getOffset(),
								refAnnotation.getEndNode().getOffset());
				if (coveringMathAnnotations.isEmpty()) {
					filteredRefAnnotations.add(refAnnotation);
				}
			}

			addNavigationalRelations(filteredRefAnnotations);

			addRestrictedRelations();

			return this.graph;
		} catch (AccessGateDocumentException e) {
			logger.error("Failed to load the document: {}", arxivId, e);
			throw new RuntimeException(e);
		} catch (AccessGateStorageException e) {
			logger.error(
					"Failed to access the storage while loading the document: {}",
					arxivId, e);
			throw new RuntimeException(e);
		} finally {
			gateDocumentDAO.release(getDocument());
			setDocument(null);
		}
	}

	private void addRestrictedRelations() {
		exemplifiesRelationAnalyzer.addRelations(graph, parsedDocument);
		provesRelationAnalyzer.addRelations(graph, parsedDocument);
		hasConsequenceRelationAnalyzer.addRelations(graph, parsedDocument);
	}

	/**
	 * TODO: now it wrongly handles 2-level containments
	 */
	private void addPartholeRelations() {
		int size = structuralElements.size();
		int refId = 0;
		for (int i = 0; i < size - 1; i++)
			for (int j = i + 1; j < size; j++) {
				if (structuralElements.get(i).getGateStartOffset() >= structuralElements
						.get(j).getGateStartOffset()
						&& structuralElements.get(i).getGateEndOffset() <= structuralElements
								.get(j).getGateEndOffset()) {
					long documentSize = getDocument().getContent().size();
					ParsedDocument refDocument = new ParsedDocumentImpl(
							getParsedDocument().getCollectionId(),
							getParsedDocument().getUri(), getParsedDocument()
									.getPdfUri(), documentSize);
					Reference reference = new ReferenceImpl.Builder(refId--)
							.document(refDocument).build();
					reference
							.setPredictedRelation(MocassinOntologyRelations.HAS_PART);
					addEdge(reference, structuralElements.get(j),
							structuralElements.get(i));
					break;
				}
			}

	}

	private ParsedDocument getParsedDocument() {
		return parsedDocument;
	}

	private void loadStructuralElements(ParsedDocument parsedDocument) {
		List<StructuralElement> elements = getStructuralElementSearcher()
				.retrieveElements(parsedDocument);
		Collections.sort(elements, new DescPositionComparator());
		setStructuralElements(elements);
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
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

	public List<Token> getTokensForAnnotation(Document document,
			Annotation annotation) {
		return getAnnotationUtil().getTokensForAnnotation(document, annotation,
				USE_STEMMING);
	}

	private void addEdge(Reference edge, final StructuralElement from,
			final StructuralElement to) {
		StructuralElement foundFrom = null;
		StructuralElement foundTo = null;
		if (this.graph.containsVertex(from)) {
			foundFrom = findVertice(from);
		}
		if (this.graph.containsVertex(to)) {
			foundTo = findVertice(to);
		}
		this.graph.addEdge(edge, foundFrom != null ? foundFrom : from,
				foundTo != null ? foundTo : to);
	}

	private StructuralElement findVertice(StructuralElement node) {
		Collection<StructuralElement> vertices = this.graph.getVertices();
		for (StructuralElement cur : vertices) {
			if (cur.equals(node)) {
				return cur;
			}
		}
		throw new RuntimeException("node not found: " + node);
	}

	private void addNavigationalRelations(Iterable<Annotation> annotations) {

		for (Annotation annotation : annotations) {
			int id = annotation.getId();
			String labelref = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.LABEL_REF_ATTRIBUTE_NAME);
			StructuralElement to = getElementByLabel(labelref);
			StructuralElement from = getEnclosingElement(annotation);
			if (to == null || from == null)
				continue;
			Annotation enclosingSentence = getAnnotationUtil()
					.getEnclosingSentence(getDocument(), annotation);
			List<Token> sentenceTokens = getTokensForAnnotation(getDocument(),
					enclosingSentence);
			long documentSize = getDocument().getContent().size();
			ParsedDocument refDocument = new ParsedDocumentImpl(
					getParsedDocument().getCollectionId(), getParsedDocument()
							.getUri(), getParsedDocument().getPdfUri(),
					documentSize);
			String additionalRefid = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.REF_ID_ATTRIBUTE_NAME);
			Reference reference = new ReferenceImpl.Builder(id)
					.document(refDocument).additionalRefid(additionalRefid)
					.build();
			reference.setSentenceTokens(sentenceTokens);
			addEdge(reference, from, to);
			Prediction prediction = navigationalRelationClassifier.predict(
					reference, graph);
			if (prediction != null) {
				reference.setPredictedRelation(prediction.getRelation());
			}
		}
	}

	private StructuralElement getEnclosingElement(Annotation annotation) {
		long refOffset = annotation.getStartNode().getOffset();
		StructuralElement closestParentElement = null;
		List<StructuralElement> elements = getStructuralElements();
		for (StructuralElement element : elements) {
			long start = element.getGateStartOffset();
			long end = element.getGateEndOffset();
			boolean isEnclosing = refOffset >= start && refOffset <= end;
			if (!isEnclosing)
				continue;
			if (closestParentElement == null) {
				closestParentElement = element;
			} else if (refOffset - start < refOffset
					- closestParentElement.getGateStartOffset()) {
				closestParentElement = element;
			}

		}

		if (closestParentElement == null) {
			getLogger()
					.warn("Parent element for reference with id='{}' in a document '{}' not found",
							annotation.getId(), getDocument().getName());
		}

		return closestParentElement;
	}

	private StructuralElement getElementByLabel(String labelref) {
		List<StructuralElement> elements = getStructuralElements();
		for (StructuralElement element : elements) {
			if (element.getLabels().contains(labelref)) {
				return element;
			}
		}
		logger.warn("Element with a label '{}' not found", labelref);
		return null;
	}

	public Logger getLogger() {
		return logger;
	}

	public List<StructuralElement> getStructuralElements() {
		return structuralElements;
	}

	public void setStructuralElements(List<StructuralElement> structuralElements) {
		this.structuralElements = structuralElements;
	}

}
