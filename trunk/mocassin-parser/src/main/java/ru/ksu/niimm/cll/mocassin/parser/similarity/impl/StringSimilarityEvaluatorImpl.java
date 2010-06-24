package ru.ksu.niimm.cll.mocassin.parser.similarity.impl;

import java.util.HashMap;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.parser.similarity.StringSimilarityEvaluator;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;

public class StringSimilarityEvaluatorImpl implements StringSimilarityEvaluator {
	private Map<SimilarityMetrics, AbstractStringMetric> type2distance = new HashMap<SimilarityMetrics, AbstractStringMetric>();

	public StringSimilarityEvaluatorImpl() {
		type2distance.put(SimilarityMetrics.LEVENSHTEIN, new Levenshtein());
		type2distance.put(SimilarityMetrics.SOUNDEX, new Soundex());
		type2distance.put(SimilarityMetrics.N_GRAM, new QGramsDistance());
	}

	@Override
	public float getSimilarity(String firstString, String secondString,
			SimilarityMetrics metricType) {
		AbstractStringMetric distance = getType2metric().get(metricType);
		return distance.getSimilarity(firstString, secondString);
	}

	private Map<SimilarityMetrics, AbstractStringMetric> getType2metric() {
		return type2distance;
	}

}
