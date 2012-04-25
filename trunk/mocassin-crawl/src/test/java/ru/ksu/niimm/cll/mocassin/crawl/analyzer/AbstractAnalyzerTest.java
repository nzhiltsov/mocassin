package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.aliasi.matrix.Vector;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
		OntologyTestModule.class, FullTextModule.class,
		GateModule.class, PdfParserModule.class })
public abstract class AbstractAnalyzerTest {
	@Inject
	private ReferenceSearcher referenceSearcher;

	private final List<Reference> references = new ArrayList<Reference>();

	@Before
	public void init() throws Exception {
		ParsedDocument document = new ParsedDocumentImpl("ivm18",
				"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18");
		Graph<StructuralElement, Reference> graph = this.referenceSearcher
				.retrieveStructuralGraph(document);
		Collection<Reference> edges = graph.getEdges();
		Assert.assertTrue("Extracted edge list is empty.", edges.size() > 0);

		for (Reference ref : edges) {
			if (!ref.getSentenceTokens().isEmpty()) {
				this.references.add(ref);
				if (this.references.size() == 2) {
					break;
				}
			}
		}

		Assert.assertEquals("Both the references haven't been found.", 2,
				this.references.size());
	}

	protected <T> void print(Map<T, Vector> map, String outputPath,
			String header) throws IOException {
		FileWriter writer = new FileWriter(outputPath);

		if (header != null) {
			writer.write(String.format("%s\n", header));
		}
		for (Entry<T, Vector> t : map.entrySet()) {
			Vector vector = t.getValue();
			String vectorStr = convertToString(vector);
			writer.write(String.format("%s %s\n", t.toString(), vectorStr));
		}
		writer.flush();
		writer.close();
	}

	protected String convertToString(Vector vector) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vector.numDimensions(); i++) {
			double value = vector.value(i);
			sb.append((double) Math.round(value * 1000) / 1000);
			sb.append(" ");
		}
		String vectorStr = sb.toString();
		return vectorStr;
	}

	protected String convertToString(SortedMap<String, Float> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Float> entry : map.entrySet()) {
			float value = entry.getValue();
			sb.append((float) Math.round(value * 1000) / 1000);
			sb.append(" ");
		}
		return sb.toString();
	}

	protected List<Reference> getReferences() {
		return references;
	}
}
