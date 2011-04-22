package unittest;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.latex.LatexSearcherParseException;
import ru.ksu.niimm.cll.mocassin.nlp.latex.LatexStructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, LatexParserModule.class, FullTextModule.class })
public class LatexStructuralElementSearcherTest {
	@Inject
	private LatexStructuralElementSearcher latexStructuralElementSearcher;

	private InputStream in;

	private ParsedDocument parsedDocument;

	@Before
	public void init() throws Exception {
		this.in = this.getClass().getResourceAsStream("/example.tex");
		parsedDocument = new ParsedDocumentImpl("http://somehost.com/doc",
				"http://localhost/example.pdf");
	}

	@Test
	public void testRetrieveGraph() throws LatexSearcherParseException {
		Graph<StructuralElement, Reference> graph = latexStructuralElementSearcher
				.retrieveGraph(this.in, this.parsedDocument, true);
		Collection<Reference> edges = graph.getEdges();
		Assert.assertTrue(edges.size() > 0);
		for (Reference ref : edges) {
			System.out.println(graph.getSource(ref) + " -> "
					+ graph.getDest(ref) + ": " + ref);
		}
		System.out.println("***");

		Collection<StructuralElement> vertices = graph.getVertices();
		StructuralElement[] verticesArray = Iterables.toArray(vertices,
				StructuralElement.class);
		Arrays.sort(verticesArray, new UriComparator());
		for (StructuralElement element : verticesArray) {
			System.out.println(element);
		}
	}

	@SuppressWarnings("serial")
	public static class UriComparator implements Comparator<StructuralElement>,
			Serializable {

		@Override
		public int compare(StructuralElement first, StructuralElement second) {
			return first.getUri().compareTo(second.getUri());
		}

	}
}
