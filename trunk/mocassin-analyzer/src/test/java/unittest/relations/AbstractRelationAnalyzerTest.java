package unittest.relations;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.w3c.dom.views.AbstractView;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

import com.csvreader.CsvReader;

public abstract class AbstractRelationAnalyzerTest  {
	private final String TEST_DATA_FILEPATH;

	private final String EVALUATION_RESULTS_OUTPUT_FILENAME;

	protected List<RelationInfo> testRecords;
	protected int successCount;
	protected int errorCount;

	public AbstractRelationAnalyzerTest(String testDataFilepath,
			String outputFilename) {
		TEST_DATA_FILEPATH = testDataFilepath;
		EVALUATION_RESULTS_OUTPUT_FILENAME = outputFilename;
	}

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

	protected void printEvaluationResults() throws IOException {
		float precision = ((float) successCount) / (successCount + errorCount);

		FileWriter fw = new FileWriter(EVALUATION_RESULTS_OUTPUT_FILENAME);
		fw.write(String.format("precision: %f; success: %d, error: %d",
				precision, successCount, errorCount));
		fw.flush();
		fw.close();
	}

}