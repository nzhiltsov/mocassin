package unittest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;
import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.Builder;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { LatexParserModule.class})
public class StructureBuilderTest {
	@Inject
	private Builder structureAnalyzer;
	@Inject
	private TreeParser treeParser;

	private LatexDocumentModel model;

	@Before
	public void init() throws LexerException, IOException {
		 InputStream in = this.getClass().getResourceAsStream("/example.tex");
		InputStreamReader reader = new InputStreamReader(in, "utf8");
		this.model = this.treeParser.parseTree(reader);
	}

	@Test
	public void testAnalyze() {
		List<Edge<Node, Node>> edges = getStructureAnalyzer().analyze(
				getModel());
		int count = 0;
		for (Edge<Node, Node> edge : edges) {
			if (edge.getContext().getEdgeType() == EdgeType.REFERS_TO) {
				count++;
				System.out.println(edge);
			}
			
		}
		Assert.assertEquals(getModel().getReferences().size(), count);
	}

	public Builder getStructureAnalyzer() {
		return structureAnalyzer;
	}

	public TreeParser getTreeParser() {
		return treeParser;
	}

	public LatexDocumentModel getModel() {
		return model;
	}

}
