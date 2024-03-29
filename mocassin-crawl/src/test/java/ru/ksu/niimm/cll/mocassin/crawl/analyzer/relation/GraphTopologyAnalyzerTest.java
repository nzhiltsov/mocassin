/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.DocumentAnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.GeneratePdfSummaryException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;
import gate.Document;
import gate.Factory;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ DocumentAnalyzerModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class, GateModule.class,
	PdfParserModule.class })
public class GraphTopologyAnalyzerTest {
    @Inject
    private GraphTopologyAnalyzer graphTopologyAnalyzer;

    @Inject
    private ReferenceSearcher referenceSearcher;
    @Inject
    private GateProcessingFacade gateProcessingFacade;
    @Inject
    private LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;
    @Inject
    private PdflatexWrapper pdflatexWrapper;
    @Inject
    private Latex2PDFMapper latex2pdfMapper;
    @Inject
    private ArxmlivProducer arxmlivProducer;

    private Graph<StructuralElement, Reference> graph;

    @Before
    public void init() throws Exception {
	Document document = prepareDoc("ivm18");
	try {
	    graph = referenceSearcher.retrieveStructuralGraph(document,
		    "http://mathnet.ru/ivm18");
	} finally {
	    if (document != null) {
		Factory.deleteResource(document);
	    }
	}
    }

    private Document prepareDoc(String documentId)
	    throws PdflatexCompilationException, GeneratePdfSummaryException {
	latexDocumentHeaderPatcher.patch(documentId);
	pdflatexWrapper.compilePatched(documentId);
	latex2pdfMapper.generateSummary(documentId);
	String arxmlivFilePath = arxmlivProducer.produce(documentId);
	return gateProcessingFacade.process(documentId, new File(
		arxmlivFilePath), "utf8");
    }

    @Test
    public void testExtractCandidateFeatures() {
	int n = graph.getVertexCount();
	List<RelationFeatureInfo> candidates = graphTopologyAnalyzer
		.extractCandidateFeatures(graph);
	Assert.assertEquals(
		"The number of candidates must be equal to the complete graph cardinality, i.e. n(n-1)/2",
		n * (n - 1) / 2, candidates.size());
	checkNeighborJaccard(candidates, 19, 1017, 0.04347826f);
	checkNeighborJaccard(candidates, 2900, 3460, 0.5f);
	checkPreferentialAttachment(candidates, 2900, 3460, 9);
	checkPreferentialAttachment(candidates, 19, 1017, 119);
	checkPageRanks(candidates);
	checkKatz(candidates, 1538, 2981, 6.25e-10f);
	checkKatz(candidates, 1017, 1196, 0.005025f);
    }

    private void checkPageRanks(List<RelationFeatureInfo> candidates) {
	Map<StructuralElement, Float> element2PR = new HashMap<StructuralElement, Float>();
	for (RelationFeatureInfo info : candidates) {
	    if (!element2PR.containsKey(info.getFrom())) {
		element2PR.put(info.getFrom(), info.getFromPR());
	    }
	    if (!element2PR.containsKey(info.getTo())) {
		element2PR.put(info.getTo(), info.getToPR());
	    }
	}
	List<Entry<StructuralElement, Float>> list = new ArrayList<Entry<StructuralElement, Float>>(
		element2PR.entrySet());
	Collections.sort(list,
		new Comparator<Entry<StructuralElement, Float>>() {
		    public int compare(Entry<StructuralElement, Float> first,
			    Entry<StructuralElement, Float> second) {
			return first.getValue().compareTo(second.getValue());
		    }
		});
	Assert.assertEquals(
		"The highest PageRank is not equal to the expected one.",
		0.0621076, list.get(list.size() - 1).getValue(), 1e-4);
    }

    private void checkPreferentialAttachment(
	    List<RelationFeatureInfo> candidates, int firstId, int secondId,
	    int expectedScore) {
	boolean foundRelation = false;

	for (RelationFeatureInfo info : candidates) {
	    foundRelation = (info.getFrom().getId() == firstId && info.getTo()
		    .getId() == secondId)
		    || (info.getFrom().getId() == secondId && info.getTo()
			    .getId() == firstId);

	    if (foundRelation) {
		Assert.assertEquals(
			"The preferential attachment score for the given two elements is not equal to the expected one.",
			expectedScore, info.getPreferentialAttachmentScore());
		break;
	    }

	}
	Assert.assertTrue(
		"Failed to find a relation between two elements with given ids.",
		foundRelation);
    }

    private void checkNeighborJaccard(List<RelationFeatureInfo> candidates,
	    int firstId, int secondId, float expectedJaccardValue) {
	boolean foundRelation = false;

	for (RelationFeatureInfo info : candidates) {
	    foundRelation = (info.getFrom().getId() == firstId && info.getTo()
		    .getId() == secondId)
		    || (info.getFrom().getId() == secondId && info.getTo()
			    .getId() == firstId);

	    if (foundRelation) {
		Assert.assertEquals(
			"The neighbor Jaccard coefficient for the given two elements is not equal to the expected one.",
			expectedJaccardValue,
			info.getNeighborJaccardCoefficient(), 1e-8);
		break;
	    }

	}
	Assert.assertTrue(
		"Failed to find a relation between two elements with given ids.",
		foundRelation);
    }

    private void checkKatz(List<RelationFeatureInfo> candidates, int firstId,
	    int secondId, float expectedKatzValue) {
	for (RelationFeatureInfo info : candidates) {
	    if (info.getFrom().getId() == firstId
		    && info.getTo().getId() == secondId) {
		Assert.assertEquals(
			"The Katz coefficient for the given two elements is not equal to the expected one.",
			expectedKatzValue, info.getKatzCoefficient(), 1e-8);
	    }
	}
    }

    @Test
    public void generateMLNFile() throws IOException,
	    GeneratePdfSummaryException, PdflatexCompilationException {
	String[] docs = { "ivm101", "ivm170", "ivm260", "ivm3", "ivm829",
		"ivm167", "ivm991", "ivm26", "ivm521", "ivm940" };

	FileWriter fstream = new FileWriter("/tmp/data.db");
	BufferedWriter out = new BufferedWriter(fstream);

	for (String doc : docs) {
	    Document document = prepareDoc(doc);
	    Graph<StructuralElement, Reference> docGraph = referenceSearcher
		    .retrieveStructuralGraph(document, "http://mathnet.ru/"
			    + doc);
	    MLNUtil.generateMLNFile(out, docGraph,
		    graphTopologyAnalyzer.extractCandidateFeatures(docGraph),
		    doc);
	    out.write("\n\n\n");
	}
	out.close();

	FileReader reader = new FileReader("/tmp/data.db");
	BufferedReader in = new BufferedReader(reader);

	boolean lemma = false;
	boolean theorem = false;

	String line = in.readLine();
	int num = 0;
	while (!line.equals("")) {
	    num++;

	    if (line.equals("Lemma(Ivm101_832)")) {
		lemma = true;
	    }

	    if (line.equals("Theorem(Ivm101_2313)")) {
		theorem = true;
	    }

	    line = in.readLine();
	}
	in.close();

	Assert.assertEquals(
		"The number of class predicates in ivm101 is not equal to the expected one",
		32, num);
	Assert.assertTrue("Lemma(Ivm101_832) predicate not found", lemma);
	Assert.assertTrue("Theorem(Ivm101_2313) predicate not found", theorem);
    }
}
