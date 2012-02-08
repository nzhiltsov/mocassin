package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivStructureElementTypes;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator.SimilarityMetrics;

class StructuralElementTypeRecognizerImpl implements
		StructuralElementTypeRecognizer {
	/**
	 * threshold for checking prediction
	 */
	private float threshold = 0.501f;

	private Set<String> structuralElementTypes = MocassinOntologyClasses
			.toSet();

	StructuralElementTypeRecognizerImpl() {
	}

	@Override
	public MocassinOntologyClasses predict(StructuralElement structuralElement) {
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
		SortedMap<String, Float> map = new TreeMap<String, Float>();
		for (String structuralElementType : getStructuralElementTypes()) {
			float similarity = StringSimilarityEvaluator.getSimilarity(name,
					structuralElementType, SimilarityMetrics.N_GRAM);
			map.put(structuralElementType, similarity);
		}
		return map;
	}

	public Set<String> getStructuralElementTypes() {
		return structuralElementTypes;
	}

}
