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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import edu.uci.ics.jung.graph.Graph;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.StructuralElementByLocationComparator;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.StructuralElementImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.StructuralElementImpl.Builder;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.StructuralElementImpl.TypeFilterPredicate;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivFormatConstants;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.crawl.parser.impl.NodeImpl.NodePositionComparator;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Node;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator.SimilarityMetrics;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Basic implementation of the structural element searcher
 * 
 * @author Nikita Zhiltsov
 * 
 */
class StructuralElementSearcherImpl implements StructuralElementSearcher {
    private final String ARXMLIV_MARKUP_NAME;
    private final String TITLE_ANNOTATION_NAME;
    @InjectLogger
    private Logger logger;
    @Inject
    private AnnotationUtil annotationUtil;
    @Inject
    private StructuralElementTypeRecognizer structuralElementTypeRecognizer;
    @Inject
    private LatexDocumentDAO latexDocumentDAO;
    @Inject
    private StructureBuilder structureBuilder;

    @Inject
    private StructuralElementSearcherImpl(
	    @Named("arxmliv.markup.name") String arxmlivMarkupName,
	    @Named("title.annotation.name") String titleAnnotationName) {
	this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
	this.TITLE_ANNOTATION_NAME = titleAnnotationName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StructuralElement> retrieveElements(Document document,
	    String paperUrl) {
	String paperId = StringUtil.extractMathnetKeyFromURI(paperUrl);
	LatexDocumentModel latexDocumentModel = latexDocumentDAO.load(paperId);
	List<Node> latexNodes = new ArrayList<Node>(structureBuilder
		.buildStructureGraph(latexDocumentModel).getVertices());
	Collections.sort(latexNodes, new NodePositionComparator());

	AnnotationSet structuralAnnotations = annotationUtil
		.getStructuralAnnotations(document);

	List<StructuralElement> elements = new ArrayList<StructuralElement>();
	for (Annotation structuralAnnotation : structuralAnnotations) {
	    StructuralElement element = extractStructuralElement(document,
		    paperUrl, structuralAnnotation, structuralAnnotations);
	    elements.add(element);
	}

	fillPageNumbers(elements, latexNodes);
	return elements;
    }

    /**
     * {@inheritDoc}
     */
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

    private void fillPageNumbers(List<StructuralElement> elements,
	    List<Node> latexNodes) {
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

    private StructuralElement getClosestPredecessor(
	    List<StructuralElement> elements, StructuralElement successorElement) {
	Collections.sort(elements, new StructuralElementByLocationComparator());

	int foundElementIndex = elements.indexOf(successorElement);

	int predecessorIndex = foundElementIndex - 1;

	return predecessorIndex > -1 ? elements.get(predecessorIndex) : null;
    }

    private String getTextContentsForAnnotation(Document document,
	    Annotation annotation) {
	return annotationUtil
		.getTextContentsForAnnotation(document, annotation);
    }

    private List<Token> getTokensForAnnotation(Document document,
	    Annotation annotation) {
	    return annotationUtil.getTokensForAnnotation(document, annotation);
    }

    private void fillElementLocation(StructuralElement element, Node node) {
	element.setStartPageNumber(node.getPdfPageNumber());
	element.setLatexStartLine(node.getBeginLine());
	element.setLatexEndLine(node.getEndLine());
    }

    private StructuralElement extractStructuralElement(Document document,
	    String paperUrl, Annotation annotation,
	    AnnotationSet structuralAnnotations) {
	Integer id = annotation.getId();
	Long start = annotation.getStartNode().getOffset();
	Long end = annotation.getEndNode().getOffset();
	String type = annotation.getType();
	String classFeature = (String) annotation.getFeatures().get(
		ArxmlivFormatConstants.CLASS_ATTRIBUTE_NAME);

	String name = classFeature != null
		&& !type.equals(ArxmlivStructureElementTypes.TABLE.toString()) ? classFeature
		: type;

	List<String> labels = collectLabels(document, annotation,
		structuralAnnotations);

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
		paperUrl + "/" + id).start(start).end(end).name(name)
		.title(title).build();
	element.setLabels(labels);
	if (id != 0) {
	    element.setContents((Token[]) getTokensForAnnotation(document, annotation).toArray(new Token[0]));
	}
	MocassinOntologyClasses predictedClass = structuralElementTypeRecognizer
		.recognize(element);
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
    private List<String> collectLabels(Document document,
	    Annotation annotation, AnnotationSet structuralAnnotations) {
	List<String> labels = extractLabels(annotation);
	AnnotationSet containedElementSet = document.getAnnotations(
		ARXMLIV_MARKUP_NAME).getContained(
		annotation.getStartNode().getOffset(),
		annotation.getEndNode().getOffset());
	ImmutableSet<Annotation> containedAxiliaryElements = Sets.difference(
		containedElementSet, structuralAnnotations).immutableCopy();
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
