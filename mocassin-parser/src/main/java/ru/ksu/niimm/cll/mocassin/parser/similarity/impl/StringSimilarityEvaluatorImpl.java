package ru.ksu.niimm.cll.mocassin.parser.similarity.impl;

import java.util.HashMap;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.parser.similarity.StringSimilarityEvaluator;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;

public class StringSimilarityEvaluatorImpl implements StringSimilarityEvaluator {
	private Map<SimilarityMetrics, AbstractStringMetric> type2distance = new HashMap<SimilarityMetrics, AbstractStringMetric>();

	public StringSimilarityEvaluatorImpl() {
		type2distance.put(SimilarityMetrics.LEVENSHTEIN, new Levenshtein());
		type2distance.put(SimilarityMetrics.SOUNDEX, new Soundex());
		type2distance.put(SimilarityMetrics.N_GRAM, new QGramsDistance());
		type2distance.put(SimilarityMetrics.JARO_WINKLER, new JaroWinkler());
		type2distance.put(SimilarityMetrics.MONGE_ELKAN, new MongeElkan());
		type2distance.put(SimilarityMetrics.NEEDLEMAN_WUNCH,
				new NeedlemanWunch());
		type2distance
				.put(SimilarityMetrics.SMITH_WATERMAN, new SmithWaterman());
	}

	@Override
	public float getSimilarity(String firstString, String secondString,
			SimilarityMetrics metricType) {
		if (!getType2metric().containsKey(metricType)) {
			throw new UnsupportedOperationException(String.format(
					"this metric isn't supported: %s", metricType));
		}
		AbstractStringMetric distance = getType2metric().get(metricType);
		return distance.getSimilarity(firstString, secondString);
	}

	private Map<SimilarityMetrics, AbstractStringMetric> getType2metric() {
		return type2distance;
	}

}
