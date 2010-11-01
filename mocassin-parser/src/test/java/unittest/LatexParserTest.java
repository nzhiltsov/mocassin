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

	private static final String COLLECTION_STATS_OUTPUT_DIR = "/tmp/collection-stats";

	private static final String DOCUMENT_COLLECTION_INPUT_DIR = "<enter dir name here>";

	@Inject
	private Parser parser;

	private InputStream in;

	private List<File> files;

	@Before
	public void init() throws FileNotFoundException {
		this.in = this.getClass().getResourceAsStream("/example.tex");

		initializeFileList();

		makeOutputDir();
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
			List<Node> nodes = getParser().getNodes();

			saveStats(file, nodes);
		}

	}

	private void saveStats(File file, List<Node> nodes) throws IOException {
		File parentFile = file.getParentFile();
		File grandParentFile = parentFile.getParentFile();
		String parentDir = parentFile.getName();
		String grandParentDir = grandParentFile.getName();
		printNodes(nodes, new File(String.format("%s/%s_%s_%s.txt",
				COLLECTION_STATS_OUTPUT_DIR, grandParentDir, parentDir, file
						.getName())));
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

	private void makeOutputDir() {
		File outputDir = new File(COLLECTION_STATS_OUTPUT_DIR);
		if (!outputDir.canRead()) {
			if (!outputDir.mkdir()) {
				throw new RuntimeException(
						String
								.format(
										"couldn't create root folder to save collection stats: %s",
										COLLECTION_STATS_OUTPUT_DIR));
			}
		}
	}
}
