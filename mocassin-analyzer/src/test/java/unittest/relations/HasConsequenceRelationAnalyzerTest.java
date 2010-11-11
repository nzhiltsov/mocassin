package unittest.relations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.csvreader.CsvReader;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyModule.class, VirtuosoModule.class })
public class HasConsequenceRelationAnalyzerTest {
	private static final String TEST_DATA_FILEPATH = "/tmp/Corollary-training-data.csv";
	@Inject
	private HasConsequenceRelationAnalyzer hasConsequenceRelationAnalyzer;

	private List<RelationInfo> testRecords;

	private int successCount;

	private int errorCount;

	@Before
	public void init() throws IOException {
		CsvReader reader = new CsvReader(TEST_DATA_FILEPATH, ';');
		reader.setTrimWhitespace(true);
		reader.readHeaders();
		List<RelationInfo> list = new ArrayList<RelationInfo>();
		while (reader.readRecord()) {
			String filename = reader.get("filename");
			int domainId = Integer.parseInt(reader.get("domain_id"));
			int rangeId = Integer.parseInt(reader.get("range_id"));
			RelationInfo record = new RelationInfo();
			record.setRelation(MocassinOntologyRelations.HAS_CONSEQUENCE);
			record.setFilename(filename);
			record.setDomainId(domainId);
			record.setRangeId(rangeId);
			list.add(record);
		}
		this.testRecords = Collections.unmodifiableList(list);
		reader.close();
	}

	@Test
	public void testAnalyze() {
		List<RelationInfo> sample = testRecords.subList(0, 10);
		List<RelationInfo> processedRecords = getHasConsequenceRelationAnalyzer()
				.analyze(sample);
		for (RelationInfo testInfo : sample) {
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

	private void printEvaluationResults() {
		float precision = ((float) successCount) / (successCount + errorCount);
		System.out.println(String.format(
				"precision: %f; success: %d, error: %d", precision,
				successCount, errorCount));
	}

	public HasConsequenceRelationAnalyzer getHasConsequenceRelationAnalyzer() {
		return hasConsequenceRelationAnalyzer;
	}

}
