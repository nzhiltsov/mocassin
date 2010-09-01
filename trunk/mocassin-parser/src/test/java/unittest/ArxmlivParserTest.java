package unittest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import unittest.util.XmlUtils;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(ArxmlivParserModule.class)
public class ArxmlivParserTest {
	@Inject
	private Parser parser;

	private File[] files;

	@Before
	public void init() throws FileNotFoundException {
		// this.in = this.getClass().getResourceAsStream("/example.tex.xml");
		File dir = new File("/OTHER_DATA/arxmliv-dataset/modified");
		this.files = dir.listFiles();
	}

	@Test
	public void testGetGraph() throws Exception {

		for (File file : getFiles()) {
			InputStream in = new FileInputStream(file);
			try {
				getParser().load(in);
				List<Edge<Node, Node>> graph = getParser().getGraph();
				save(graph, file.getName());
			} finally {
				in.close();
			}
		}
	}

	private static void save(List<Edge<Node, Node>> graph, String filename)
			throws JAXBException, IOException {
		/*
		 * JaxbUtil.marshall(new GraphContainer(String.format(
		 * "/tmp/ref-contexts/%s-refcontext.xml", filename), graph));
		 */
		XmlUtils.save(new GraphContainer(String.format(
				"/tmp/ref-contexts/%s-refcontext.xml", filename), graph));
	}

	public Parser getParser() {
		return parser;
	}

	public File[] getFiles() {
		return files;
	}

}
