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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.*;

import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator.SimilarityMetrics;

/**
 * The class detects the type of a given structural element relying on its
 * markup properties and the n-gram string similarity algorithm. 
 * 
 * @author Nikita Zhiltsov
 * 
 */
class StructuralElementTypeRecognizerImpl implements
	StructuralElementTypeRecognizer {
    /**
     * Threshold for the string similarity algorithm
     */
    private float threshold = 0.501f;

    private Set<String> structuralElementTypes = MocassinOntologyClasses
	    .toSet();
    /**
     * {@inheritDoc}
     */
    @Override
    public MocassinOntologyClasses recognize(StructuralElement structuralElement) {
	// special handling of the root element
	if (structuralElement.getName() != null
		&& structuralElement.getName().equals(
			ArxmlivStructureElementTypes.DOCUMENT.toString())) {
	    return MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT;
	}
	// special handling of sections
	if (Arrays.asList(MocassinOntologyClasses.SECTION.getLabels())
		.contains(structuralElement.getName())) {
	    return MocassinOntologyClasses.SECTION;
	}

	String name = structuralElement.getTitle() == null
		|| structuralElement.getTitle().length() == 0 ? structuralElement
		.getName() : new StringTokenizer(structuralElement.getTitle())
		.nextToken();
	SortedMap<String, Float> similarityVector = computeSimilarityVector(name);
	float maxValue = Float.NEGATIVE_INFINITY;
	String maxName = null;
	for (Map.Entry<String, Float> entry : similarityVector.entrySet()) {
	    if (entry.getValue() > maxValue) {
		maxValue = entry.getValue();
		maxName = entry.getKey();
	    }
	}
	if (maxName == null)
	    throw new RuntimeException(
		    "list of structural element types is empty");
	return maxValue >= threshold ? MocassinOntologyClasses
		.fromLabel(maxName)
		: MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT;
    }

    private SortedMap<String, Float> computeSimilarityVector(String name) {
	checkNotNull(name);
	String checkedName = name.trim();
	SortedMap<String, Float> map = new TreeMap<String, Float>();
	for (String structuralElementType : getStructuralElementTypes()) {
	    float similarity = StringSimilarityEvaluator.getSimilarity(checkedName,
		    structuralElementType, SimilarityMetrics.N_GRAM);
	    map.put(structuralElementType, similarity);
	}
	return map;
    }

    private Set<String> getStructuralElementTypes() {
	return structuralElementTypes;
    }

}
