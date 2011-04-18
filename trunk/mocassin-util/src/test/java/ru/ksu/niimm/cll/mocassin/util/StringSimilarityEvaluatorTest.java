package ru.ksu.niimm.cll.mocassin.util;
import java.util.List;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator.SimilarityMetrics;

import com.google.common.collect.ImmutableList;

public class StringSimilarityEvaluatorTest {
	private List<String> labels = ImmutableList.of("assertion", "assumption",
			"conjecture", "corollary", "definition", "document", "example",
			"lemma", "proof", "proposition", "theorem");

	@Test
	public void testSimilarity() {

		final String nodeName = "lemmann";

		for (String label : getLabels()) {
			float levenshtein = StringSimilarityEvaluator.getSimilarity(
					nodeName, label, SimilarityMetrics.LEVENSHTEIN);
			float soundex = StringSimilarityEvaluator.getSimilarity(nodeName,
					label, SimilarityMetrics.SOUNDEX);
			float ngram = StringSimilarityEvaluator.getSimilarity(nodeName,
					label, SimilarityMetrics.N_GRAM);
			float jarowinkler = StringSimilarityEvaluator.getSimilarity(
					nodeName, label, SimilarityMetrics.JARO_WINKLER);
			float mongeelkan = StringSimilarityEvaluator.getSimilarity(
					nodeName, label, SimilarityMetrics.MONGE_ELKAN);
			float needlemanwunch = StringSimilarityEvaluator.getSimilarity(
					nodeName, label, SimilarityMetrics.NEEDLEMAN_WUNCH);
			float smithwaterman = StringSimilarityEvaluator.getSimilarity(
					nodeName, label, SimilarityMetrics.SMITH_WATERMAN);
			String info = String.format(
					"%s | %s | %f | %f | %f | %f | %f | %f | %f", nodeName,
					label, levenshtein, soundex, ngram, jarowinkler,
					mongeelkan, needlemanwunch, smithwaterman);
			System.out.println(info);
		}

	}

	public Iterable<String> getLabels() {
		return labels;
	}

}
