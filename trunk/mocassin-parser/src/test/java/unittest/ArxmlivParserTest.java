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
	private static final String EXAMPLE_FILENAME = "src/test/resources/example.tex.xml";

	private static final String REF_CONTEXTS_OUTPUT_FOLDER = "/tmp/ref-contexts";

	@Inject
	private Parser parser;

	private File[] files;

	@Before
	public void init() throws FileNotFoundException {
		File exampleFile = new File(EXAMPLE_FILENAME);
		this.files = new File[] { exampleFile };
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
		String cuttedFileName = cutExtension(filename);
		XmlUtils.save(new GraphContainer(REF_CONTEXTS_OUTPUT_FOLDER,
				cuttedFileName, graph));
	}

	private static String cutExtension(String filename) {
		return filename.substring(0, filename.indexOf("."));
	}

	public Parser getParser() {
		return parser;
	}

	public File[] getFiles() {
		return files;
	}

}
