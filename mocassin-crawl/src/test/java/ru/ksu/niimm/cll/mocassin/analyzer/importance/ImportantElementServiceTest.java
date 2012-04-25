package ru.ksu.niimm.cll.mocassin.analyzer.importance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.ImportantElementService;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
		OntologyTestModule.class, GateModule.class, PdfParserModule.class })
public class ImportantElementServiceTest {
	@Inject
	private ReferenceSearcher referenceSearcher;
	@Inject
	private ImportantElementService importantElementService;

	private Graph<StructuralElement, Reference> graph;

	@Before
	public void init() throws Exception {
		ParsedDocument document = new ParsedDocumentImpl("ivm18",
				"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18");
		this.graph = this.referenceSearcher.retrieveStructuralGraph(document);
	}

	@Test
	public void testComputeImportanceRanks() {
		Map<StructuralElement, Float> element2score = importantElementService
				.computeImportanceRanks(graph);
		checkScores(element2score);
	}

	private static void checkScores(Map<StructuralElement, Float> node2score) {
		List<Entry<StructuralElement, Float>> elements = new ArrayList<Map.Entry<StructuralElement, Float>>(
				node2score.entrySet());
		Collections.sort(elements, new EntryByDescScoreComparator());
		Assert.assertEquals(
				"The actual most important element does not equal to the expected one.",
				185, elements.get(0).getKey().getId());
		Assert.assertEquals(
				"The score of the actual most important element does not equal to the expected one's.",
				0.056211073f, elements.get(0).getValue());
	}

	private static class EntryByDescScoreComparator implements
			Comparator<Entry<StructuralElement, Float>> {

		@Override
		public int compare(Entry<StructuralElement, Float> first,
				Entry<StructuralElement, Float> second) {
			return second.getValue().compareTo(first.getValue());
		}

	}
}
