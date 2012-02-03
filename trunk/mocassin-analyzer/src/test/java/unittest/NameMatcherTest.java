package unittest;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.Mapping;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.MappingElement;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;

import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, VirtuosoModule.class, FullTextModule.class,
		GateModule.class, PdfParserModule.class })
public class NameMatcherTest {
	@Inject
	private Parser parser;
	@Inject
	private StructureBuilder structureBuilder;
	@Inject
	private Matcher matcher;
	private Graph<Node, Edge> graph;

	@Before
	public void init() throws Exception {
		InputStream in = this.getClass().getResourceAsStream("/example.tex");

		LatexDocumentModel model = parser.parse("example", in, true);

		graph = this.structureBuilder.buildStructureGraph(model);
	}

	@Test
	public void testDoMapping() {
		Mapping mapping = getMatcher().doMapping(getGraph());
		print(mapping);
	}

	private void print(Mapping mapping) {
		List<MappingElement> elements = mapping.getElements();
		Comparator<MappingElement> byNodeName = new Comparator<MappingElement>() {

			@Override
			public int compare(MappingElement o1, MappingElement o2) {
				return o1.getNode().getName().compareTo(o2.getNode().getName());
			}
		};

		Comparator<MappingElement> byConceptUri = new Comparator<MappingElement>() {

			@Override
			public int compare(MappingElement o1, MappingElement o2) {
				return o1.getConcept().getUri()
						.compareTo(o2.getConcept().getUri());
			}
		};

		Ordering<MappingElement> mappingElementOrdering = Ordering.from(
				byNodeName).compound(byConceptUri);

		List<MappingElement> sortedElements = mappingElementOrdering
				.sortedCopy(elements);

		for (MappingElement element : sortedElements) {
			System.out.println(element);
		}
	}

	public Matcher getMatcher() {
		return matcher;
	}

	public Graph<Node, Edge> getGraph() {
		return graph;
	}

}
