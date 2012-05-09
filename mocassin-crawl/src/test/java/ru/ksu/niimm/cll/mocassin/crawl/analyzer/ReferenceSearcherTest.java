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
import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ProcessException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.GeneratePdfSummaryException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ DocumentAnalyzerModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class, GateModule.class,
	PdfParserModule.class })
public class ReferenceSearcherTest {
    private static final String FIRST_DOC_ID = "ivm18";
    private static final String FIRST_DOC_URL = String.format(
	    "http://mathnet.ru/%s", FIRST_DOC_ID);
    private static final String SECOND_DOC_ID = "ivm537";
    private static final String SECOND_DOC_URL = String.format(
	    "http://mathnet.ru/%s", SECOND_DOC_ID);
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

    private Document firstDocument;

    private Document secondDocument;

    @Before
    public void init() throws Exception {
	firstDocument = prepareDoc("ivm18");
	secondDocument = prepareDoc("ivm537");
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
    public void testRetrieveReferences() throws AccessGateDocumentException,
	    IOException, AccessGateStorageException, ProcessException {
	checkFirstDocument();
	checkSecondDocument();
    }

    private void checkFirstDocument() {
	Graph<StructuralElement, Reference> graph = this.referenceSearcher
		.retrieveStructuralGraph(firstDocument, FIRST_DOC_URL);
	Collection<Reference> edges = graph.getEdges();
	Assert.assertTrue("The reference list is empty", edges.size() > 0);

	boolean foundHasPartInstance = false;
	for (Reference ref : edges) {
	    foundHasPartInstance = graph.getSource(ref).getId() == 1017
		    && graph.getDest(ref).getId() == 2900;
	    if (foundHasPartInstance) {
		break;
	    }
	}

	Assert.assertTrue("The 'hasPart' instance hasn't been found.",
		foundHasPartInstance);

	int tableCount = 0;
	Collection<StructuralElement> elements = graph.getVertices();
	for (StructuralElement element : elements) {
	    if (element.getPredictedClass() == MocassinOntologyClasses.TABLE) {
		tableCount++;
	    }
	}
	Assert.assertEquals(
		"The number of tables in the document does not equal to the expected one.",
		3, tableCount);

	boolean foundFollowedByInstance = false;
	for (Reference ref : edges) {
	    foundFollowedByInstance = graph.getSource(ref).getId() == 19
		    && graph.getDest(ref).getId() == 1017
		    && ref.getPredictedRelation() == MocassinOntologyRelations.FOLLOWED_BY;
	    if (foundFollowedByInstance) {
		break;
	    }
	}
	Assert.assertTrue("The 'followedBy' instance hasn't been found.",
		foundFollowedByInstance);
	
	
    }

    private void checkSecondDocument() {
	Graph<StructuralElement, Reference> graph = this.referenceSearcher
		.retrieveStructuralGraph(secondDocument, SECOND_DOC_URL);
	Collection<Reference> edges = graph.getEdges();
	Assert.assertTrue("The reference list is empty", edges.size() > 0);

	boolean foundFollowedByInstance = false;
	for (Reference ref : edges) {
	    foundFollowedByInstance = graph.getSource(ref).getId() == 897
		    && graph.getDest(ref).getId() == 1082
		    && ref.getPredictedRelation() == MocassinOntologyRelations.FOLLOWED_BY;
	    if (foundFollowedByInstance) {
		break;
	    }
	}
	Assert.assertTrue("The 'followedBy' instance hasn't been found.",
		foundFollowedByInstance);
    }

}
