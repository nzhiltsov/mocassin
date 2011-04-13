package unittest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;
import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { LatexParserModule.class })
public class StructureBuilderTest {
	@Inject
	private StructureBuilder structureAnalyzer;

	private InputStream in;

	@Before
	public void init() throws LexerException, IOException {
		this.in = this.getClass().getResourceAsStream("/example.tex");
	}

	@Test
	public void testAnalyze() {
		List<Edge<Node, Node>> edges = getStructureAnalyzer()
				.buildStructureGraph(this.in);
		for (Edge<Node, Node> edge : edges) {

			System.out.println(String.format("%s | %s | %s", edge.getFrom()
					.getName(), edge.getTo().getName(), edge.getContext()
					.getEdgeType()));
		}
		Assert.assertTrue(edges.size() > 0);
	}

	public StructureBuilder getStructureAnalyzer() {
		return structureAnalyzer;
	}

}
