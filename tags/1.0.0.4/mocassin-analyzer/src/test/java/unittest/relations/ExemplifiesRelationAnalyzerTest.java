package unittest.relations;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyModule.class, VirtuosoModule.class })
public class ExemplifiesRelationAnalyzerTest extends
		AbstractRelationAnalyzerTest {
	@Inject
	ExemplifiesRelationAnalyzer exemplifiesRelationAnalyzer;

	public ExemplifiesRelationAnalyzerTest() {
		super("/tmp/Exemplifies-training-data.csv",
				"/tmp/exemplifies-results.txt");
	}

	@Test
	public void testAnalyze() throws IOException {
		List<RelationInfo> processedRecords = exemplifiesRelationAnalyzer
				.analyze(testRecords);
		for (RelationInfo testInfo : testRecords) {
			for (RelationInfo processedInfo : processedRecords) {
				if (processedInfo.getFilename().equals(testInfo.getFilename())
						&& processedInfo.getDomainId() == testInfo
								.getDomainId()) {
					boolean isValid = processedInfo.getRangeId() == testInfo
							.getRangeId();
					if (isValid) {
						successCount++;
					} else {
						errorCount++;
					}
				}
			}
		}
		printEvaluationResults();
	}

}
