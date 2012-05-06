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

import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses.SECTION;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses.THEOREM;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses.getUri;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations.HAS_PART;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.model.URIConstants.createRdfTypeTriple;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.model.URIConstants.createTriple;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ProcessException;
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

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;
import gate.Document;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
	OntologyTestModule.class, FullTextModule.class, GateModule.class,
	PdfParserModule.class })
public class ReferenceStatementGeneratorTest {
    @Inject
    protected ReferenceStatementGenerator referenceStatementGenerator;
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
    public void init() throws Exception, AccessGateStorageException,
	    ProcessException {
	Document document = prepareDoc("ivm18");
	graph = this.referenceSearcher.retrieveStructuralGraph(document,
		"http://mathnet.ru/ivm18");
	Collection<Reference> edges = graph.getEdges();
	Assert.assertTrue("The reference list is empty", edges.size() > 0);
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
    public void testConvert() {
	List<Statement> statements = referenceStatementGenerator.convert(graph);
	final String sectionInstance = "http://mathnet.ru/ivm18/1017";
	final String theoremInstance = "http://mathnet.ru/ivm18/2900";
	Statement hasPartStatement = createTriple(sectionInstance,
		HAS_PART.getUri(), theoremInstance);
	Assert.assertTrue(
		"Generated statement list does contain the 'hasPart' statement.",
		statements.contains(hasPartStatement));
	Statement hasSectionInstanceType = createRdfTypeTriple(sectionInstance,
		getUri(SECTION));
	Assert.assertTrue(
		"Generated statement list does contain any statement about the section instance type.",
		statements.contains(hasSectionInstanceType));
	Statement hasTheoremInstanceType = createRdfTypeTriple(theoremInstance,
		getUri(THEOREM));
	Assert.assertTrue(
		"Generated statement list does contain any statement about the theorem instance type.",
		statements.contains(hasTheoremInstanceType));
    }
}
