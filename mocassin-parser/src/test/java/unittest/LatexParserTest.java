package unittest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(LatexParserModule.class)
public class LatexParserTest {
	@Inject
	private Logger logger;

	private static final String COLLECTION_STATS_OUTPUT_FILE = "/tmp/collection-stats.txt";

	private static final String DOCUMENT_COLLECTION_INPUT_DIR = "<enter dir name here>";

	@Inject
	private Parser parser;

	private InputStream in;

	private List<File> files;

	@Before
	public void init() throws FileNotFoundException {
		this.in = this.getClass().getResourceAsStream("/example.tex");

		initializeFileList();

	}

	@Test
	public void testGetGraph() throws Exception {
		getParser().load(getInputStream());
		List<Edge<Node, Node>> graph = getParser().getGraph();
		Assert.assertTrue(!graph.isEmpty());
		print(graph);
	}

	@Test
	public void testGetNodes() throws Exception {
		List<Node> nodes = new ArrayList<Node>();
		for (File file : getFiles()) {
			InputStream inputStream = new FileInputStream(file);
			try {
				getParser().load(inputStream);
			} catch (Throwable e) {
				logger.log(Level.SEVERE, String.format(
						"parsing of the file failed: %s", file
								.getAbsolutePath()));
				continue;
			}
			nodes.addAll(getParser().getNodes());

		}
		printNodes(nodes, new File(COLLECTION_STATS_OUTPUT_FILE));
	}

	public Parser getParser() {
		return parser;
	}

	public InputStream getInputStream() {
		return in;
	}

	public List<File> getFiles() {
		return files;
	}

	/**
	 * 
	 * @param nodes
	 *            nodes to save
	 * @param file
	 *            file for saving
	 * @throws IOException
	 */
	private void printNodes(List<Node> nodes, File file) throws IOException {
		Map<String, Integer> node2count = new HashMap<String, Integer>();
		for (Node node : nodes) {
			Integer value = node2count.containsKey(node.getName()) ? node2count
					.get(node.getName()) : 0;
			node2count.put(node.getName(), value + 1);
		}

		FileWriter writer = new FileWriter(file);
		writer.write("element count\n");
		for (String nodeName : node2count.keySet()) {
			Integer value = node2count.get(nodeName);
			writer.write(String.format("%s %d\n", nodeName, value));
		}
		writer.flush();
		writer.close();

	}

	private void print(List<Edge<Node, Node>> graph) {
		for (Edge<Node, Node> edge : graph) {
			System.out.println(edge);
		}
		System.out.println(graph.size());
	}

	private void initializeFileList() {
		File dir = new File(DOCUMENT_COLLECTION_INPUT_DIR);
		this.files = new ArrayList<File>();
		Stack<File> stack = new Stack<File>();
		stack.push(dir);
		while (!stack.isEmpty()) {
			File f = stack.pop();
			if (f.isFile()) {
				files.add(f);
			} else if (f.isDirectory()) {
				for (File child : f.listFiles()) {
					stack.push(child);
				}
			}
		}
	}

}
