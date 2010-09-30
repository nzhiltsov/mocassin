package unittest;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StringSimilarityEvaluator.SimilarityMetrics;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyModule.class, VirtuosoModule.class })
public class StringSimilarityEvaluatorTest {
	@Inject
	private StringSimilarityEvaluator stringSimilarityEvaluator;
	private List<String> labels = ImmutableList.of("assertion", "assumption",
			"conjecture", "corollary", "definition", "document", "example",
			"lemma", "proof", "proposition", "theorem");

	@Test
	public void testSimilarity() {

		final String nodeName = "lemmann";

		for (String label : getLabels()) {
			float levenshtein = getStringSimilarityEvaluator().getSimilarity(
					nodeName, label, SimilarityMetrics.LEVENSHTEIN);
			float soundex = getStringSimilarityEvaluator().getSimilarity(
					nodeName, label, SimilarityMetrics.SOUNDEX);
			float ngram = getStringSimilarityEvaluator().getSimilarity(
					nodeName, label, SimilarityMetrics.N_GRAM);
			float jarowinkler = getStringSimilarityEvaluator().getSimilarity(
					nodeName, label, SimilarityMetrics.JARO_WINKLER);
			float mongeelkan = getStringSimilarityEvaluator().getSimilarity(
					nodeName, label, SimilarityMetrics.MONGE_ELKAN);
			float needlemanwunch = getStringSimilarityEvaluator()
					.getSimilarity(nodeName, label,
							SimilarityMetrics.NEEDLEMAN_WUNCH);
			float smithwaterman = getStringSimilarityEvaluator().getSimilarity(
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

	public StringSimilarityEvaluator getStringSimilarityEvaluator() {
		return stringSimilarityEvaluator;
	}

}
