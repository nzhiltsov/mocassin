package unittest;

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
import org.junit.Ignore;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyModule.class, VirtuosoModule.class,
		FullTextModule.class })
@Ignore
public class AbstractRankingTest {
	@Inject
	private StructureBuilder structureBuilder;
	@Inject
	private ImportantNodeService importantNodeService;

	private List<Graph<Node, Edge>> models = new LinkedList<Graph<Node, Edge>>();

	@Before
	public void init() throws LexerException, IOException {
		InputStream in = this.getClass().getResourceAsStream("/example.tex");

		this.models.add(this.structureBuilder.buildStructureGraph(in, true));

	}

	protected void printScores(Map<Node, Float> node2score) {
		for (Entry<Node, Float> node : node2score.entrySet()) {
			System.out.println(String.format("%s: %f", node.toString(),
					node2score.get(node.getValue())));
		}
	}

	protected static Map<Node, Float> sortByValue(Map<Node, Float> map) {
		List<Entry<Node, Float>> list = new LinkedList<Entry<Node, Float>>(map
				.entrySet());
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
