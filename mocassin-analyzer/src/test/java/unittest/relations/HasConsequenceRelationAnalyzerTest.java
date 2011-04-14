package unittest.relations;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
		OntologyModule.class, VirtuosoModule.class })
@Ignore("it's necessary to switch from GATE-based implementation")
public class HasConsequenceRelationAnalyzerTest extends
		AbstractRelationAnalyzerTest {
	@Inject
	private HasConsequenceRelationAnalyzer hasConsequenceRelationAnalyzer;

	public HasConsequenceRelationAnalyzerTest() {
		super("/tmp/Corollary-training-data.csv",
				"/tmp/hasConsequence-results.txt");
	}

	@Test
	public void testAnalyze() throws IOException {
		List<RelationInfo> processedRecords = getHasConsequenceRelationAnalyzer()
				.analyze(testRecords);
		for (RelationInfo testInfo : testRecords) {
			for (RelationInfo processedInfo : processedRecords) {
				if (processedInfo.getFilename().equals(testInfo.getFilename())
						&& processedInfo.getRangeId() == testInfo.getRangeId()) {
					boolean isValid = processedInfo.getDomainId() == testInfo
							.getDomainId();
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

	public HasConsequenceRelationAnalyzer getHasConsequenceRelationAnalyzer() {
		return hasConsequenceRelationAnalyzer;
	}

}
