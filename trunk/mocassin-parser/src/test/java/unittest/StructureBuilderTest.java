package unittest;

import java.io.IOException;
import java.io.InputStream;

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

import edu.uci.ics.jung.graph.Hypergraph;

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
	public void testGraphEdges() {
		Hypergraph<Node, Edge> graph = getStructureAnalyzer()
				.buildStructureGraph(this.in, true);
		Assert.assertTrue(graph.getEdgeCount() > 0);
		for (Edge edge : graph.getEdges()) {
			Node from = graph.getSource(edge);
			Node to = graph.getDest(edge);
			System.out.println(String.format("%s | %s | %s", from.getName(), to
					.getName(), edge.getContext().getEdgeType()));
		}
		System.out.println("***");

	}

	@Test
	public void testGraphNodes() {
		Hypergraph<Node, Edge> graph = getStructureAnalyzer()
				.buildStructureGraph(this.in, true);
		Assert.assertTrue(graph.getVertexCount() > 0);

		for (Node node : graph.getVertices()) {
			System.out.println(node);
		}

		System.out.println("***");

	}

	public StructureBuilder getStructureAnalyzer() {
		return structureAnalyzer;
	}

}
