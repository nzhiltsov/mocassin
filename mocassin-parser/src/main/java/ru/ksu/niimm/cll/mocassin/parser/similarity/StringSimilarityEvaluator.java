package ru.ksu.niimm.cll.mocassin.parser.similarity;

public interface StringSimilarityEvaluator {
	enum SimilarityMetrics {
		LEVENSHTEIN, SOUNDEX, N_GRAM, JARO_WINKLER, MONGE_ELKAN, NEEDLEMAN_WUNCH, SMITH_WATERMAN
	}

	float getSimilarity(String firstString, String secondString,
			SimilarityMetrics metric);
}
