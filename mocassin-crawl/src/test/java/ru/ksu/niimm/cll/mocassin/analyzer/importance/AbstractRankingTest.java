package ru.ksu.niimm.cll.mocassin.analyzer.importance;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.junit.Before;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Node;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Parser;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class,
		GateModule.class, PdfParserModule.class })
public abstract class AbstractRankingTest {
	@Inject
	private Parser parser;
	@Inject
	private StructureBuilder structureBuilder;
	@Inject
	private ImportantNodeService importantNodeService;

	private List<Graph<Node, Edge>> models = new LinkedList<Graph<Node, Edge>>();

	@Before
	public void init() throws LexerException, IOException {
		InputStream in = this.getClass().getResourceAsStream("/example.tex");
		LatexDocumentModel latexDocumentModel = this.parser.parse("example",
				in, "utf8", true);

		this.models.add(this.structureBuilder
				.buildStructureGraph(latexDocumentModel));

	}

	protected void printScores(Map<Node, Float> node2score) {
		for (Entry<Node, Float> node : node2score.entrySet()) {
			System.out.println(String.format("%s: %f", node.toString(),
					node2score.get(node.getValue())));
		}
	}

	protected static Map<Node, Float> sortByValue(Map<Node, Float> map) {
		List<Entry<Node, Float>> list = new LinkedList<Entry<Node, Float>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Entry<Node, Float>>() {

			@Override
			public int compare(Entry<Node, Float> o1, Entry<Node, Float> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});
		Map<Node, Float> result = new LinkedHashMap<Node, Float>();
		for (Iterator<Entry<Node, Float>> it = list.iterator(); it.hasNext();) {
			Entry<Node, Float> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public ImportantNodeService getImportantNodeService() {
		return importantNodeService;
	}

	public List<Graph<Node, Edge>> getModels() {
		return models;
	}
}
