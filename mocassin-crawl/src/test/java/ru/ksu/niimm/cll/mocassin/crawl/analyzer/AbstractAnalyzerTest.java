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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import edu.uci.ics.jung.graph.Graph;
import gate.Document;

import java.io.File;
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

import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.GeneratePdfSummaryException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.aliasi.matrix.Vector;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ DocumentAnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class, GateModule.class,
	PdfParserModule.class })
public abstract class AbstractAnalyzerTest {
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

    private final List<Reference> references = new ArrayList<Reference>();

    @Before
    public void init() throws Exception {
	Document document = prepareDoc("ivm18");
	Graph<StructuralElement, Reference> graph = this.referenceSearcher
		.retrieveStructuralGraph(document, "http://mathnet.ru/ivm18");
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

    private Document prepareDoc(String documentId)
	    throws PdflatexCompilationException, GeneratePdfSummaryException {
	latexDocumentHeaderPatcher.patch(documentId);
	pdflatexWrapper.compilePatched(documentId);
	latex2pdfMapper.generateSummary(documentId);
	String arxmlivFilePath = arxmlivProducer.produce(documentId);
	return gateProcessingFacade.process(documentId, new File(
		arxmlivFilePath), "utf8");
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
