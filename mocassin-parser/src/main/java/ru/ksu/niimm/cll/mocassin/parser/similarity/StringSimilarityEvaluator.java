package ru.ksu.niimm.cll.mocassin.parser.similarity;

public interface StringSimilarityEvaluator {
	enum SimilarityMetrics {
		LEVENSHTEIN, SOUNDEX, N_GRAM
	}

	float getSimilarity(String firstString, String secondString,
			SimilarityMetrics metric);
}
