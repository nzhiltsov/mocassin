package unittest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.LatexParserImpl;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(LatexParserModule.class)
public class LatexParserTest {
	@Inject
	private Parser parser;

	private InputStream in;

	@Before
	public void init() throws FileNotFoundException {
		this.in = this.getClass().getResourceAsStream("/example.tex");

	}

	@Test
	public void testGetGraph() throws Exception {
		getParser().load(getInputStream());
		List<Edge<Node, Node>> graph = getParser().getGraph();
		Assert.assertTrue(!graph.isEmpty());
		print(graph);
	}

	private void print(List<Edge<Node, Node>> graph) {
		for (Edge<Node, Node> edge : graph) {
			System.out.println(edge);
		}
		System.out.println(graph.size());
	}

	public Parser getParser() {
		return parser;
	}

	public InputStream getInputStream() {
		return in;
	}

}
