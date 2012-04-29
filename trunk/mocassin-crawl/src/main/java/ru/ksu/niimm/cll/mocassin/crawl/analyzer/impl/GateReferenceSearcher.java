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
		String arxivId = StringUtil.arxivid2gateid(parsedDocument
				.getCollectionId());
		Document document = null;
		try {
			Graph<StructuralElement, Reference> graph = new DirectedSparseMultigraph<StructuralElement, Reference>();

			document = gateDocumentDAO.load(arxivId);

			List<StructuralElement> elements = getStructuralElementSearcher()
					.retrieveElements(parsedDocument);
			Collections.sort(elements, new DescPositionComparator());

			addPartholeRelations(graph, elements, document, parsedDocument);
            addFollowedByRelations(graph, elements, document, parsedDocument);

			AnnotationSet refAnnotations = document.getAnnotations(
					ARXMLIV_MARKUP_NAME).get(ARXMLIV_REF_ANNOTATION_NAME);
			List<Annotation> filteredRefAnnotations = new ArrayList<Annotation>();
			for (Annotation refAnnotation : refAnnotations) {
				AnnotationSet coveringMathAnnotations = document
						.getAnnotations(ARXMLIV_MARKUP_NAME).getCovering(
								ARXMLIV_MATH_ANNOTATION_NAME,
								refAnnotation.getStartNode().getOffset(),
								refAnnotation.getEndNode().getOffset());
				if (coveringMathAnnotations.isEmpty()) {
					filteredRefAnnotations.add(refAnnotation);
				}
			}

			addNavigationalRelations(graph, elements, filteredRefAnnotations,
					document, parsedDocument);

			addRestrictedRelations(graph, parsedDocument);

			return graph;
		} catch (AccessGateDocumentException e) {
			logger.error("Failed to load the document: {}", arxivId, e);
			throw new RuntimeException(e);
		} catch (AccessGateStorageException e) {
			logger.error(
					"Failed to access the storage while loading the document: {}",
					arxivId, e);
			throw new RuntimeException(e);
		} finally {
			gateDocumentDAO.release(document);
		}
	}

	private void addRestrictedRelations(
			Graph<StructuralElement, Reference> graph,
			ParsedDocument parsedDocument) {
		exemplifiesRelationAnalyzer.addRelations(graph, parsedDocument);
		provesRelationAnalyzer.addRelations(graph, parsedDocument);
		hasConsequenceRelationAnalyzer.addRelations(graph, parsedDocument);
	}

	/**
	 * TODO: now it wrongly handles 2-level containments
	 * 
	 * @param graph
	 * 
	 * @param structuralElements
	 * @param parsedDocument
	 * @param document
	 */
	private void addPartholeRelations(
			Graph<StructuralElement, Reference> graph,
			List<StructuralElement> structuralElements, Document document,
			ParsedDocument parsedDocument) {
		int size = structuralElements.size();
		int refId = 0;
		for (int i = 0; i < size - 1; i++)
			for (int j = i + 1; j < size; j++) {
				if (structuralElements.get(i).getGateStartOffset() >= structuralElements
						.get(j).getGateStartOffset()
						&& structuralElements.get(i).getGateEndOffset() <= structuralElements
								.get(j).getGateEndOffset()) {
					long documentSize = document.getContent().size();
					ParsedDocument refDocument = new ParsedDocumentImpl(
							parsedDocument.getCollectionId(),
							parsedDocument.getUri(),
							parsedDocument.getPdfUri(), documentSize);
					Reference reference = new ReferenceImpl.Builder(refId--)
							.document(refDocument).build();
					reference
							.setPredictedRelation(MocassinOntologyRelations.HAS_PART);
					addEdge(graph, reference, structuralElements.get(j),
							structuralElements.get(i));
					break;
				}
			}

	}

    private void addFollowedByRelations(Graph<StructuralElement, Reference> graph,
                                        List<StructuralElement> structuralElements, Document document,
                                        ParsedDocument parsedDocument) {
        int size = structuralElements.size();
        int refId = -10000;
        for (int i = 0; i < size - 1; i++) {
            long iStart = structuralElements.get(i).getGateStartOffset();
            for (int j = i + 1; j < size; j++) {

                if(!sameLevel(graph, structuralElements.get(i), structuralElements.get(j))) {
                    continue;
                }
                long jEnd = structuralElements.get(j).getGateEndOffset();

                boolean followed = true;
                for (int k = 0; k < size && followed; k++) {
                    if (k != i && k != j) {
                        long kStart = structuralElements.get(k).getGateStartOffset();
                        long kEnd = structuralElements.get(k).getGateEndOffset();

                        if (jEnd <= kStart && kStart <= iStart) {
                            followed = false;
                        } else if (jEnd <= kEnd && kEnd <= iStart) {
                            followed = false;
                        }
                    }
                }

                if (followed) {
                    long documentSize = document.getContent().size();
                    ParsedDocument refDocument = new ParsedDocumentImpl(
                            parsedDocument.getCollectionId(),
                            parsedDocument.getUri(), parsedDocument
                            .getPdfUri(), documentSize);
                    Reference reference = new ReferenceImpl.Builder(refId--)
                            .document(refDocument).build();
                    reference
                            .setPredictedRelation(MocassinOntologyRelations.FOLLOWED_BY);
                    addEdge(graph, reference, structuralElements.get(j),
                            structuralElements.get(i));

                    break;
                }
            }
        }
    }

    private boolean sameLevel(Graph<StructuralElement, Reference> graph,
                              StructuralElement first, StructuralElement second) {
        ArrayList<Reference> firstList = new ArrayList<Reference>(graph.getInEdges(first));
        ArrayList<Reference> secondList = new ArrayList<Reference>(graph.getInEdges(second));

        if (firstList.size() == secondList.size()) {
            if (firstList.size() != 0 &&
                    graph.getOpposite(first, firstList.get(0)).getId() !=
                    graph.getOpposite(second, secondList.get(0)).getId()) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	public AnnotationUtil getAnnotationUtil() {
		return annotationUtil;
	}

	public List<Token> getTokensForAnnotation(Document document,
			Annotation annotation) {
		return getAnnotationUtil().getTokensForAnnotation(document, annotation,
				USE_STEMMING);
	}

	private void addEdge(Graph<StructuralElement, Reference> graph,
			Reference edge, final StructuralElement from,
			final StructuralElement to) {
		StructuralElement foundFrom = null;
		StructuralElement foundTo = null;
		if (graph.containsVertex(from)) {
			foundFrom = findVertice(graph, from);
		}
		if (graph.containsVertex(to)) {
			foundTo = findVertice(graph, to);
		}
		graph.addEdge(edge, foundFrom != null ? foundFrom : from,
				foundTo != null ? foundTo : to);
	}

	private StructuralElement findVertice(
			Graph<StructuralElement, Reference> graph, StructuralElement node) {
		Collection<StructuralElement> vertices = graph.getVertices();
		for (StructuralElement cur : vertices) {
			if (cur.equals(node)) {
				return cur;
			}
		}
		throw new RuntimeException("node not found: " + node);
	}

	private void addNavigationalRelations(
			Graph<StructuralElement, Reference> graph,
			List<StructuralElement> elements, Iterable<Annotation> annotations,
			Document document, ParsedDocument parsedDocument) {

		for (Annotation annotation : annotations) {
			int id = annotation.getId();
			String labelref = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.LABEL_REF_ATTRIBUTE_NAME);
			StructuralElement to = getElementByLabel(elements, labelref);
			StructuralElement from = getEnclosingElement(elements, annotation,
					document);
			if (to == null || from == null)
				continue;
			Annotation enclosingSentence = getAnnotationUtil()
					.getEnclosingSentence(document, annotation);
			List<Token> sentenceTokens = getTokensForAnnotation(document,
					enclosingSentence);
			long documentSize = document.getContent().size();
			ParsedDocument refDocument = new ParsedDocumentImpl(
					parsedDocument.getCollectionId(), parsedDocument.getUri(),
					parsedDocument.getPdfUri(), documentSize);
			String additionalRefid = (String) annotation.getFeatures().get(
					ArxmlivFormatConstants.REF_ID_ATTRIBUTE_NAME);
			Reference reference = new ReferenceImpl.Builder(id)
					.document(refDocument).additionalRefid(additionalRefid)
					.build();
			reference.setSentenceTokens(sentenceTokens);
			addEdge(graph, reference, from, to);
			Prediction prediction = navigationalRelationClassifier.predict(
					reference, graph);
			if (prediction != null) {
				reference.setPredictedRelation(prediction.getRelation());
			}
		}
	}

	private StructuralElement getEnclosingElement(
			List<StructuralElement> elements, Annotation annotation,
			Document document) {
		long refOffset = annotation.getStartNode().getOffset();
		StructuralElement closestParentElement = null;
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
							annotation.getId(), document.getName());
		}

		return closestParentElement;
	}

	private StructuralElement getElementByLabel(
			List<StructuralElement> elements, String labelref) {
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

}
