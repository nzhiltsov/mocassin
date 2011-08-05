package ru.ksu.niimm.cll.mocassin.nlp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, GateModule.class, LatexParserModule.class,
		PdfParserModule.class })
public class CitationSearcherTest {
	@Inject
	private CitationSearcher citationSearcher;

	private final List<String> ids = new LinkedList<String>();

	@Before
	public void init() throws IOException {
		CsvReader reader = new CsvReader(new InputStreamReader(this.getClass()
				.getClassLoader().getResourceAsStream("mathnet_selected.csv")));
		reader.setTrimWhitespace(true);
		try {
			while (reader.readRecord()) {
				String id = reader.get(0);
				ids.add(id);
			}
		} finally {
			reader.close();
		}
	}

	@Test
	public void testGetCitationSentences() throws IOException {
		Multimap<String, String> key2citations = LinkedListMultimap.create();
		List<String> sample = new ArrayList<String>();
		sample.add("ivm1772");
		for (String mathnetKey : sample) {
			List<String> citationSentences = citationSearcher
					.getCitationSentences(mathnetKey);
			for (String citation : citationSentences) {
				key2citations.put(mathnetKey, citation);
			}
		}
		printCitations(key2citations);
	}

	private void printCitations(Multimap<String, String> key2citations)
			throws IOException {
		CsvWriter writer = new CsvWriter(new FileWriter(
				"/tmp/mathnet_citations.csv"), ';');
		for (Entry<String, String> entry : key2citations.entries()) {
			String[] record = { entry.getKey(), "'" + entry.getValue() + "'" };
			writer.writeRecord(record);
		}
		writer.flush();
		writer.close();
	}
}
