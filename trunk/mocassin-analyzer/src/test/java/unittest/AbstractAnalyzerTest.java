package unittest;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.aliasi.matrix.Vector;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { AnalyzerModule.class, NlpModule.class,
		LatexParserModule.class, OntologyModule.class, VirtuosoModule.class,
		FullTextModule.class })
@Ignore("references should be read from a store")
public abstract class AbstractAnalyzerTest {
	private static final String REF_CONTEXT_DATA_INPUT_FOLDER = "/tmp/refcontexts-data";

	@Inject
	private Logger logger;

	@Inject
	private ReferenceSearcher referenceSearcher;

	private final List<Reference> references = new ArrayList<Reference>();

	@Before
	public void init() throws Exception {
		ParsedDocument document = new ParsedDocumentImpl("math_0205003",
				"http://arxiv.org/pdf/math/0205003");
		Graph<StructuralElement, Reference> graph = this.referenceSearcher
				.retrieveReferences(document);
		Collection<Reference> edges = graph.getEdges();
		Assert.assertTrue(edges.size() > 0);

		for (Reference ref : edges) {
			if (ref.getPredictedRelation() == null) {
				this.references.add(ref);
			}

		}
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
