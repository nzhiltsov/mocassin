package ru.ksu.niimm.cll.mocassin.analyzer.similarity.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StructuralElementTypesInfo;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StringSimilarityEvaluator.SimilarityMetrics;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.util.IOUtils;

import com.google.inject.Inject;

public class StructuralElementTypeRecognizerImpl implements
		StructuralElementTypeRecognizer {
	@Inject
	private StringSimilarityEvaluator stringSimilarityEvaluator;

	private Set<String> structuralElementTypes;

	public StructuralElementTypeRecognizerImpl() throws IOException {
		ClassLoader loader = StructuralElementTypeRecognizerImpl.class
				.getClassLoader();
		URL url = loader.getResource("structural_elements.properties");
		this.structuralElementTypes = IOUtils.readLineSet(url.openStream());
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
