/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.StructuralElementImpl.DescPositionComparator;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.Prediction;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

/**
 * The class exploits GATE annotations and a set of associated analyzers to
 * retrieve the structural graph for a given document
 * 
 * @author Nikita Zhiltsov
 * @author Azat Khasanshin
 * 
 */
public class GateBasedReferenceSearcher implements ReferenceSearcher {
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
    private NavigationalRelationClassifier navigationalRelationClassifier;
    @Inject
    private ProvesRelationAnalyzer provesRelationAnalyzer;
    @Inject
    private HasConsequenceRelationAnalyzer hasConsequenceRelationAnalyzer;
    @Inject
    private ExemplifiesRelationAnalyzer exemplifiesRelationAnalyzer;

    @Inject
    private GateBasedReferenceSearcher(
	    @Named("arxmliv.markup.name") String arxmlivMarkupName,
	    @Named("arxmliv.ref.annotation.name") String arxmlivRefAnnotationName,
	    @Named("arxmliv.math.annotation.name") String arxmlivMathAnnotationName,
	    @Named("useStemming") String useStemming) {
	this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
	this.ARXMLIV_REF_ANNOTATION_NAME = arxmlivRefAnnotationName;
	this.ARXMLIV_MATH_ANNOTATION_NAME = arxmlivMathAnnotationName;
	this.USE_STEMMING = Boolean.parseBoolean(useStemming);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph<StructuralElement, Reference> retrieveStructuralGraph(
	    Document document, String paperUrl) {

	Graph<StructuralElement, Reference> graph = new DirectedSparseMultigraph<StructuralElement, Reference>();

	List<StructuralElement> elements = getStructuralElementSearcher()
		.retrieveElements(document, paperUrl);
	Collections.sort(elements, new DescPositionComparator());

	addPartholeRelations(graph, elements, document, paperUrl);
	addFollowedByRelations(graph, elements, document, paperUrl);

	List<Annotation> filteredRefAnnotations = filterReferenceAnnotations(document);

	addNavigationalRelations(graph, elements, filteredRefAnnotations,
		document, paperUrl);

	addRestrictedRelations(graph, paperUrl);

	return graph;

    }

    private List<Annotation> filterReferenceAnnotations(Document document) {
	AnnotationSet refAnnotations = document.getAnnotations(
		ARXMLIV_MARKUP_NAME).get(ARXMLIV_REF_ANNOTATION_NAME);
	List<Annotation> filteredRefAnnotations = new ArrayList<Annotation>();
	for (Annotation refAnnotation : refAnnotations) {
	    AnnotationSet coveringMathAnnotations = document.getAnnotations(
		    ARXMLIV_MARKUP_NAME).getCovering(
		    ARXMLIV_MATH_ANNOTATION_NAME,
		    refAnnotation.getStartNode().getOffset(),
		    refAnnotation.getEndNode().getOffset());
	    if (coveringMathAnnotations.isEmpty()) {
		filteredRefAnnotations.add(refAnnotation);
	    }
	}
	return filteredRefAnnotations;
    }

    private void addRestrictedRelations(
	    Graph<StructuralElement, Reference> graph, String paperUrl) {
	exemplifiesRelationAnalyzer.addRelations(graph, paperUrl);
	provesRelationAnalyzer.addRelations(graph, paperUrl);
	hasConsequenceRelationAnalyzer.addRelations(graph, paperUrl);
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
	    String paperUrl) {
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
			    paperUrl, documentSize);
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

    private void addFollowedByRelations(
	    Graph<StructuralElement, Reference> graph,
	    List<StructuralElement> structuralElements, Document document,
	    String paperUrl) {
	int size = structuralElements.size();
	int refId = -10000;
	for (int i = 0; i < size - 1; i++) {
	    long iStart = structuralElements.get(i).getGateStartOffset();
	    for (int j = i + 1; j < size; j++) {

		if (!sameLevel(graph, structuralElements.get(i),
			structuralElements.get(j))) {
		    continue;
		}
		long jEnd = structuralElements.get(j).getGateEndOffset();

		boolean followed = true;
		for (int k = 0; k < size && followed; k++) {
		    if (k != i
			    && k != j
			    && sameLevel(graph, structuralElements.get(i),
				    structuralElements.get(k))) {
			long kStart = structuralElements.get(k)
				.getGateStartOffset();
			long kEnd = structuralElements.get(k)
				.getGateEndOffset();

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
			    paperUrl, documentSize);
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
	ArrayList<Reference> firstList = new ArrayList<Reference>(
		graph.getInEdges(first));
	ArrayList<Reference> secondList = new ArrayList<Reference>(
		graph.getInEdges(second));

	if (firstList.size() == secondList.size()) {
	    if (firstList.size() != 0
		    && graph.getOpposite(first, firstList.get(0)).getId() != graph
			    .getOpposite(second, secondList.get(0)).getId()) {
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
	    Document document, String paperUrl) {

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
	    ParsedDocument refDocument = new ParsedDocumentImpl(paperUrl,
		    documentSize);
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
