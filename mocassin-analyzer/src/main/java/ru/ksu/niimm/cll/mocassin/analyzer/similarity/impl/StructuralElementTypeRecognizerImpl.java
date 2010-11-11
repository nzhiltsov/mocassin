package ru.ksu.niimm.cll.mocassin.analyzer.similarity.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypesInfo;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StringSimilarityEvaluator.SimilarityMetrics;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.util.IOUtils;

import com.google.inject.Inject;

public class StructuralElementTypeRecognizerImpl implements
		StructuralElementTypeRecognizer {
	@Inject
	private StringSimilarityEvaluator stringSimilarityEvaluator;

	private Set<String> structuralElementTypes;

	public StructuralElementTypeRecognizerImpl() throws IOException {
		this.structuralElementTypes = MocassinOntologyClasses.toSet();
	}

	@Override
	public StructuralElementTypesInfo recognize(Reference reference) {
		SortedMap<String, Float> fromElementVector = computeSimilarityVector(reference
				.getFrom().getName());
		SortedMap<String, Float> toElementVector = computeSimilarityVector(reference
				.getTo().getName());
		StructuralElementTypesInfo info = new StructuralElementTypesInfoImpl(
				reference, fromElementVector, toElementVector);
		return info;
	}

	@Override
	public MocassinOntologyClasses predict(StructuralElement structuralElement) {
		SortedMap<String, Float> similarityVector = computeSimilarityVector(structuralElement
				.getTitleTokens().get(0).getValue());
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
		return MocassinOntologyClasses.fromString(maxName);
	}

	private SortedMap<String, Float> computeSimilarityVector(String name) {
		SortedMap<String, Float> map = new TreeMap<String, Float>();
		for (String structuralElementType : getStructuralElementTypes()) {
			float similarity = getStringSimilarityEvaluator().getSimilarity(
					name.toLowerCase(), structuralElementType,
					SimilarityMetrics.N_GRAM);
			map.put(structuralElementType, similarity);
		}
		return map;
	}

	public StringSimilarityEvaluator getStringSimilarityEvaluator() {
		return stringSimilarityEvaluator;
	}

	public Set<String> getStructuralElementTypes() {
		return structuralElementTypes;
	}

}
