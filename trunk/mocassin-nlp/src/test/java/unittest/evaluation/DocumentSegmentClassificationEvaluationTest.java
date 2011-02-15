package unittest.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.TokenImpl;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

import com.csvreader.CsvReader;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class DocumentSegmentClassificationEvaluationTest {
	private static final String TEST_DATA_FILEPATH = "/tmp/arxmliv-element-training-data.csv";
	private static final String EVALUATION_RESULTS_OUTPUT_FILENAME = "/tmp/arxmliv-element-recognition-results.txt";
	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	private List<StructuralElement> testRecords;

	private ContingencyTable table = new ContingencyTable();

	@Before
	public void init() throws IOException {
		CsvReader reader = new CsvReader(TEST_DATA_FILEPATH, ';');
		reader.setTrimWhitespace(true);
		reader.readHeaders();
		List<StructuralElement> list = new ArrayList<StructuralElement>();
		while (reader.readRecord()) {
			String prediction = reader.get("prediction");
			String name = reader.get("name");
			String title = reader.get("title");
			MocassinOntologyClasses predictedClass = prediction.equals("null") ? null
					: MocassinOntologyClasses.fromString(prediction);
			int id = Integer.parseInt(reader.get("id"));
			StructuralElement element = new StructuralElementImpl.Builder(id)
					.name(name).build();
			element.setPredictedClass(predictedClass);
			StringTokenizer st = new StringTokenizer(title, " -");
			List<Token> titleTokens = new ArrayList<Token>();
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				Token titleToken = new TokenImpl(token, null);
				titleTokens.add(titleToken);
			}
			element.setTitleTokens(titleTokens);
			list.add(element);
		}
		this.testRecords = Collections.unmodifiableList(list);
		reader.close();
	}

	@Test
	public void evaluate() throws IOException {

		for (StructuralElement element : testRecords) {
			MocassinOntologyClasses predictedClassByAlgorithm = structuralElementTypeRecognizer
					.predict(element);
			table.put(predictedClassByAlgorithm, element.getPredictedClass());
		}
		printEvaluationTable();
	}

	private float computeAvgFMeasure() {
		int n = table.matrix.length;
		float avgFmeasure = 0;
		for (MocassinOntologyClasses clazz : MocassinOntologyClasses.values()) {
			int i = clazz.ordinal();
			int rowSum = 0;
			for (int k = 0; k < n; k++) {
				rowSum += table.matrix[i][k];
			}
			int columnSum = 0;
			for (int k = 0; k < n; k++) {
				columnSum += table.matrix[k][i];
			}
			float precision = ((float) table.matrix[i][i]) / rowSum;
			float recall = ((float) table.matrix[i][i]) / columnSum;
			float fmeasure = 2 * (precision * recall) / (precision + recall);

			avgFmeasure += fmeasure;

		}

		return avgFmeasure / MocassinOntologyClasses.values().length;
	}

	private void printEvaluationTable() throws IOException {
		FileWriter writer = new FileWriter(EVALUATION_RESULTS_OUTPUT_FILENAME);
		writer.write("name precision recall f-measure\n");
		int n = table.matrix.length;
		float avgFmeasure = 0;
		for (MocassinOntologyClasses clazz : MocassinOntologyClasses.values()) {
			int i = clazz.ordinal();
			int rowSum = 0;
			for (int k = 0; k < n; k++) {
				rowSum += table.matrix[i][k];
			}
			int columnSum = 0;
			for (int k = 0; k < n; k++) {
				columnSum += table.matrix[k][i];
			}
			float precision = ((float) table.matrix[i][i]) / rowSum;
			float recall = ((float) table.matrix[i][i]) / columnSum;
			float fmeasure = 2 * (precision * recall) / (precision + recall);

			avgFmeasure += fmeasure;

			writer.write(String.format("%s %f %f %f\n", clazz.toString(),
					precision, recall, fmeasure));
		}

		writer.write(String.format("%f", avgFmeasure
				/ MocassinOntologyClasses.values().length));

		writer.flush();
		writer.close();
	}
}