package unittest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import unittest.LatexStructuralElementSearcherTest.UriComparator;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { NlpModule.class, LatexParserModule.class, FullTextModule.class })
public class ReferenceSearcherTest {
	@Inject
	private Logger logger;
	@Inject
	private ReferenceSearcher referenceSearcher;

	@Test
	public void testRetrieveReferences() throws AccessGateDocumentException,
			IOException {
		ParsedDocument document = new ParsedDocumentImpl("math/0205003", "http://arxiv.org/abs/math/0205003",
		"http://arxiv.org/pdf/math/0205003");
		Graph<StructuralElement, Reference> graph = this.referenceSearcher
				.retrieveReferences(document);
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
			logger.log(Level.INFO, String.format("%s \"%s\" %d", element,
					element.getTitle(), element.getStartPageNumber()));
		}
	}

}
