package ru.ksu.niimm.cll.mocassin.util;

import java.util.Map;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;

import com.google.common.collect.ImmutableMap;

public class StringSimilarityEvaluator {
	public enum SimilarityMetrics {
		LEVENSHTEIN, SOUNDEX, N_GRAM, JARO_WINKLER, MONGE_ELKAN, NEEDLEMAN_WUNCH, SMITH_WATERMAN
	}

	private static Map<SimilarityMetrics, AbstractStringMetric> type2distance = ImmutableMap
			.<SimilarityMetrics, AbstractStringMetric> builder().put(
					SimilarityMetrics.LEVENSHTEIN, new Levenshtein()).put(
					SimilarityMetrics.SOUNDEX, new Soundex()).put(
					SimilarityMetrics.N_GRAM, new QGramsDistance()).put(
					SimilarityMetrics.JARO_WINKLER, new JaroWinkler()).put(
					SimilarityMetrics.MONGE_ELKAN, new MongeElkan()).put(
					SimilarityMetrics.NEEDLEMAN_WUNCH, new NeedlemanWunch())
			.put(SimilarityMetrics.SMITH_WATERMAN, new SmithWaterman()).build();

	/**
	 * returns similarity values for given pair of strings according to given
	 * string similarity algorithm; low case variants of the strings are
	 * considered
	 * 
	 * @param firstString
	 * @param secondString
	 * @param metricType
	 * @return
	 */
	public static float getSimilarity(String firstString, String secondString,
			SimilarityMetrics metricType) {
		if (!getType2metric().containsKey(metricType)) {
			throw new UnsupportedOperationException(String.format(
					"this metric isn't supported: %s", metricType));
		}
		AbstractStringMetric distance = getType2metric().get(metricType);
		return distance.getSimilarity(firstString.toLowerCase(), secondString
				.toLowerCase());
	}

	private static Map<SimilarityMetrics, AbstractStringMetric> getType2metric() {
		return type2distance;
	}

}
