package ru.ksu.niimm.cll.mocassin.nlp.gate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.impl.NotInMathPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.PositionComparator;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivFormatConstants;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

public class GateReferenceSearcher implements ReferenceSearcher {
	@Inject
	private Logger logger;
	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;
	@Inject
	private AnnotationUtil annotationUtil;

	@Inject
	private GateDocumentDAO gateDocumentDAO;

	private List<StructuralElement> structuralElements;

	private Graph<StructuralElement, Reference> graph;

	private Document document;

	private ParsedDocument parsedDocument;

	@Override
	public synchronized Graph<StructuralElement, Reference> retrieveStructuralGraph(
			ParsedDocument parsedDocument) {
		this.parsedDocument = parsedDocument;
		String arxivId = parsedDocument.getArxivId().replace("/", "_");
		try {
			this.graph = new DirectedSparseMultigraph<StructuralElement, Reference>();

			setDocument(gateDocumentDAO.load(arxivId));

			loadStructuralElements(parsedDocument);

			addPartholeRelations();

			AnnotationSet refAnnotations = document
					.getAnnotations(
							getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
					.get(getProperty(GateFormatConstants.ARXMLIV_REF_ANNOTATION_PROPERTY_KEY));
			Iterable<Annotation> filteredRefAnnotations = Iterables.filter(
					refAnnotations, new NotInMathPredicate(
							getNlpModulePropertiesLoader(), getDocument()));

			addNavigationalRelations(filteredRefAnnotations);

			return this.graph;
		} catch (AccessGateDocumentException e) {
			logger.log(Level.SEVERE,
					String.format("failed to load the document: %s", arxivId));
			throw new RuntimeException(e);
		} finally {
			gateDocumentDAO.release(getDocument());
		}
	}

	private void addPartholeRelations() {
		int size = structuralElements.size();
		int refId = 0;
		for (int i = 0; i < size; i++)
			for (int j = i + 1; j < size
					&& structuralElements.get(i).getEnd() >= structuralElements
							.get(j).getStart(); j++) {
				if (structuralElements.get(i).getStart() <= structuralElements
						.get(j).getStart()
						&& structuralElements.get(i).getEnd() >= structuralElements
								.get(j).getEnd()) {
					long documentSize = getDocument().getContent().size();
					ParsedDocument refDocument = new ParsedDocumentImpl(
							getParsedDocument().getArxivId(), getParsedDocument()
									.getUri(), getParsedDocument().getPdfUri(),
							documentSize);
					Reference reference = new ReferenceImpl.Builder(refId--)
							.document(refDocument).build();
					reference
							.setPredictedRelation(MocassinOntologyRelations.HAS_PART);
					addEdge(reference, structuralElements.get(i),
							structuralElements.get(j));
				}
			}

	}

	private ParsedDocument getParsedDocument() {
		return parsedDocument;
	}

	private void loadStructuralElements(ParsedDocument parsedDocument) {
		List<StructuralElement> elements = getStructuralElementSearcher()
				.retrieveElements(parsedDocument);
		Collections.sort(elements, new PositionComparator());
		setStructuralElements(elements);
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

	public List<Token> getTokensForAnnotation(Document document,
			Annotation annotation) {
		return getAnnotationUtil().getTokensForAnnotation(document, annotation,
				getNlpModulePropertiesLoader().useStemming());
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

	private synchronized void addNavigationalRelations(
			Iterable<Annotation> annotations) {

		for (Annotation annotation : annotations) {
			int id = annotation.getId();
			String labelref = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.LABEL_REF_ATTRIBUTE_NAME);
			StructuralElement to = getElementByLabel(labelref);
			StructuralElement from = getEnclosingElement(annotation);
			if (to == null || from == null)
				continue;
			Annotation enclosingSentence = getEnclosingSentence(annotation);
			List<Token> sentenceTokens = getTokensForAnnotation(getDocument(),
					enclosingSentence);
			long documentSize = getDocument().getContent().size();
			ParsedDocument refDocument = new ParsedDocumentImpl(
					getParsedDocument().getArxivId(), getParsedDocument()
							.getUri(), getParsedDocument().getPdfUri(),
					documentSize);
			String additionalRefid = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.REF_ID_ATTRIBUTE_NAME);
			Reference reference = new ReferenceImpl.Builder(id)
					.document(refDocument).additionalRefid(additionalRefid)
					.build();
			reference.setSentenceTokens(sentenceTokens);
			addEdge(reference, from, to);
		}
	}

	private Annotation getEnclosingSentence(Annotation annotation) {
		AnnotationSet sentenceSet = getDocument()
				.getAnnotations(GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
				.getCovering(
						getProperty(GateFormatConstants.SENTENCE_ANNOTATION_NAME_PROPERTY_KEY),
						annotation.getStartNode().getOffset(),
						annotation.getEndNode().getOffset());
		if (sentenceSet.size() == 0) {
			AnnotationSet allSentences = getDocument()
					.getAnnotations(
							GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
					.get(getProperty(GateFormatConstants.SENTENCE_ANNOTATION_NAME_PROPERTY_KEY));
			long distance = Long.MAX_VALUE;
			Annotation closestSentence = null;
			for (Annotation sentence : allSentences) {
				long endDistance = Math.abs(sentence.getEndNode().getOffset()
						- annotation.getStartNode().getOffset());
				long startDistance = Math.abs(sentence.getStartNode()
						.getOffset() - annotation.getStartNode().getOffset());
				long minDistance = Math.min(endDistance, startDistance);
				if (minDistance < distance) {
					closestSentence = sentence;
					distance = minDistance;
				}
			}
			if (closestSentence == null) {
				throw new RuntimeException(String.format(
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
			getLogger()
					.log(Level.INFO,
							String.format(
									"parent element for ref with id='%d' in document '%s' not found",
									annotation.getId(), getDocument().getName()));

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
		logger.log(Level.INFO,
				String.format("element with label '%s' not found", labelref));
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