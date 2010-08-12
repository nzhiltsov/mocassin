package unittest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.ParserModule;
import ru.ksu.niimm.cll.mocassin.parser.applications.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { ParserModule.class, OntologyModule.class, VirtuosoModule.class })
public class AbstractRankingTest {
	private static final String DOCS_DIR = "/OTHER_DATA/arxiv_papers/main_results";
	private static final String DOC_ID = "f000022.tex";
	@Inject
	private StructureBuilder structureAnalyzer;
	@Inject
	private ImportantNodeService importantNodeService;
	@Inject
	private TreeParser treeParser;

	private List<LatexDocumentModel> models = new LinkedList<LatexDocumentModel>();

	@Before
	public void init() throws LexerException, IOException {
		InputStream in = this.getClass().getResourceAsStream("/example.tex");
		InputStreamReader reader = new InputStreamReader(in, "utf8");
		LatexDocumentModel model = this.treeParser.parseTree(reader);
		model.setDocId("example.tex");
		this.models.add(model);
		/*
		 * File dir = new File(DOCS_DIR); File[] docs = dir.listFiles();
		 * 
		 * for (File doc : docs) { InputStream in = new
		 * FileInputStream(String.format( "%s/%s", DOCS_DIR, doc.getName()));
		 * InputStreamReader reader = new InputStreamReader(in, "utf8");
		 * LatexDocumentModel model = this.treeParser.parseTree(reader);
		 * model.setDocId(doc.getName()); this.models.add(model); }
		 */

	}

	protected void printScores(Map<Node, Float> node2score) {
		for (Node node : node2score.keySet()) {
			System.out.println(String.format("%s: %f", node.toString(),
					node2score.get(node)));
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

	public StructureBuilder getStructureAnalyzer() {
		return structureAnalyzer;
	}

	public ImportantNodeService getImportantNodeService() {
		return importantNodeService;
	}

	public TreeParser getTreeParser() {
		return treeParser;
	}

	public List<LatexDocumentModel> getModels() {
		return models;
	}
}
