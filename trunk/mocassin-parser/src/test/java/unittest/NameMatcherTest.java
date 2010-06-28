package unittest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.ParserModule;
import ru.ksu.niimm.cll.mocassin.parser.mapping.Mapping;
import ru.ksu.niimm.cll.mocassin.parser.mapping.MappingElement;
import ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { ParserModule.class, OntologyModule.class, VirtuosoModule.class })
public class NameMatcherTest {
	@Inject
	private Parser parser;
	@Inject
	private Matcher matcher;
	private List<Edge<Node, Node>> graph;

	@Before
	public void init() throws Exception {
		InputStream in = this.getClass().getResourceAsStream("/example.tex");
		Reader reader = new InputStreamReader(in);
		getParser().load(reader);
		graph = getParser().getGraph();
	}

	@Test
	public void testDoMapping() {
		Mapping mapping = getMatcher().doMapping(getGraph());
//		print(mapping);
	}

	private void print(Mapping mapping) {
		List<MappingElement> elements = mapping.getElements();
		Collections.sort(elements, new ConfidenceComparator());
		for (MappingElement element : elements) {
			System.out.println(element);
		}

	}

	public Parser getParser() {
		return parser;
	}

	public Matcher getMatcher() {
		return matcher;
	}

	public List<Edge<Node, Node>> getGraph() {
		return graph;
	}

	private static class ConfidenceComparator implements
			Comparator<MappingElement> {

		@Override
		public int compare(MappingElement o1, MappingElement o2) {
			if (o1.getConfidence() == o2.getConfidence()) {
				return 0;
			}
			return o1.getConfidence() > o2.getConfidence() ? -1 : 1;
		}

	}
}
