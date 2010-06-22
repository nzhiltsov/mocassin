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
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.ParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(ParserModule.class)
public class ParserTest {
	@Inject
	private Parser parser;

	private Reader reader;

	@Before
	public void init() throws FileNotFoundException {
		InputStream in = this.getClass().getResourceAsStream("/example.tex");
		this.reader = new InputStreamReader(in);
	}

	@Test
	public void testGetGraph() throws Exception {
		getParser().load(getReader());
		List<Edge<Node, Node>> graph = getParser().getGraph();
		Assert.assertTrue(!graph.isEmpty());
//		print(graph);
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

	public Reader getReader() {
		return reader;
	}

}
