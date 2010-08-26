package unittest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.ArxmlivParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import unittest.util.GraphContainer;
import unittest.util.JaxbUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(ArxmlivParserModule.class)
public class ArxmlivParserTest {
	@Inject
	private Parser parser;

	private InputStream in;

	@Before
	public void init() throws FileNotFoundException {
		this.in = this.getClass().getResourceAsStream("/example.tex.xml");
	}

	@Test
	public void testGetGraph() throws Exception {

		getParser().load(getIn());
		List<Edge<Node, Node>> graph = getParser().getGraph();
		Assert.assertTrue(!graph.isEmpty());
		// save(graph, file.getName());
	}

	private static void save(List<Edge<Node, Node>> graph, String filename)
			throws FileNotFoundException, JAXBException {
		JaxbUtil.marshall(new GraphContainer(String.format(
				"/tmp/ref-contexts/%s-refcontext.xml", filename), graph));
	}

	public Parser getParser() {
		return parser;
	}

	public InputStream getIn() {
		return in;
	}

}
