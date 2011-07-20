package unittest;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import unittest.LatexStructuralElementSearcherTest.UriComparator;

import com.csvreader.CsvWriter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, LatexParserModule.class, FullTextModule.class })
public class ReferenceSearcherTest {
	@Inject
	private Logger logger;
	@Inject
	private ReferenceSearcher referenceSearcher;
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private StopWordLoader stopWordLoader;
	private List<String> documentIds;

	@Before
	public void init() throws AccessGateDocumentException, AccessGateStorageException {
		documentIds = gateDocumentDAO.getDocumentIds();
	}

	@Test
	public void testRetrieveReferences() throws AccessGateDocumentException,
			IOException {
		ParsedDocument document = new ParsedDocumentImpl("math/0001036",
				"http://arxiv.org/abs/math/0001036",
				"http://arxiv.org/pdf/math/0001036");
		Graph<StructuralElement, Reference> graph = this.referenceSearcher
				.retrieveStructuralGraph(document);
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

	@Test
	@Ignore
	public void testReferentialVerbsDistribution() throws IOException {
		Multiset<String> termSet = HashMultiset.create();
		int totalNumber = documentIds.size();
		int i = 0;
		for (String id : documentIds) {
			String gateId = id.substring(0, id.lastIndexOf(".tex.xml"));
			ParsedDocumentImpl doc = new ParsedDocumentImpl(gateId,
					"http://localhost/" + gateId, "");
			try {

				Graph<StructuralElement, Reference> graph = this.referenceSearcher
						.retrieveStructuralGraph(doc);
				Collection<Reference> edges = graph.getEdges();
				for (Reference ref : edges) {
					if (ref.getPredictedRelation() == null) {
						List<Token> terms = ref.getSentenceTokens();
						for (Token token : terms) {
							if (token.getValue().length() <= 1
									|| stopWordLoader.isStopWord(token
											.getValue())
									|| !token.getPos().startsWith("VB")) {
								continue;
							}
							termSet.add(token.getValue());
						}
					}
				}
				i++;
				logger.log(Level.INFO, String.format(
						"%d out of %d documents have been processed", i, totalNumber));
			} catch (Exception e) {
				logger.log(Level.SEVERE, String.format(
						"couldn't process the document: %s", gateId));
			}

		}
		save(termSet);
	}

	private void save(Multiset<String> termSet) throws IOException {
		CsvWriter writer = new CsvWriter(new FileWriter(
				"/home/linglab/arxmliv/saab-ref-verb-distribution.csv"), ';');
		for (Entry<String> termEntry : termSet.entrySet()) {
			String[] record = { termEntry.getElement(),
					String.valueOf(termEntry.getCount()) };
			writer.writeRecord(record);
		}
		writer.flush();
		writer.close();
	}
}
