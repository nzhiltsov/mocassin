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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.Prediction;
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
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
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
public class NavigationalRelationClassifierTest {
    @Inject
    private NavigationalRelationClassifier navigationalRelationClassifier;
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

    private Reference knownRefersToReference;

    private Reference knownDependsOnReference;

    @Before
    public void init() throws Exception {
	Document document = prepareDoc("ivm18");
	try {
	    graph = this.referenceSearcher.retrieveStructuralGraph(document,
		    "http://mathnet.ru/ivm18");
	    Collection<Reference> references = graph.getEdges();
	    Assert.assertTrue(references.size() > 0);
	    boolean foundFirst = false;
	    boolean foundSecond = false;
	    Iterator<Reference> it = references.iterator();
	    while (it.hasNext() && !(foundFirst && foundSecond)) {
		Reference ref = it.next();
		if (ref.getId() == 5086 || ref.getId() == 5087) {
		    this.knownRefersToReference = ref;
		    foundFirst = true;
		} else if (ref.getId() == 4766) {
		    this.knownDependsOnReference = ref;
		    foundSecond = true;
		}
	    }
	} finally {
	    if (document != null) {
		Factory.deleteResource(document);
	    }
	}
	Assert.assertNotNull("The known 'refersTo' reference is null",
		this.knownRefersToReference);
	Assert.assertNotNull("The known 'dependsOn' reference is null",
		this.knownDependsOnReference);
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
    public void testPredictRefersTo() {
	Prediction prediction = navigationalRelationClassifier.predict(
		knownRefersToReference, graph);
	Assert.assertEquals(
		"The predicted relation is not of the expected type.",
		MocassinOntologyRelations.REFERS_TO, prediction.getRelation());

    }

    @Test
    public void testPredictDependsOn() {
	Prediction prediction = navigationalRelationClassifier.predict(
		knownDependsOnReference, graph);
	Assert.assertEquals(
		"The predicted relation is not of the expected type.",
		MocassinOntologyRelations.DEPENDS_ON, prediction.getRelation());
    }

}
