package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import edu.uci.ics.jung.graph.Graph;
import gate.Document;

import java.io.File;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElementImpl.IdPredicate;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.GeneratePdfSummaryException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, GateModule.class, LatexParserModule.class,
	PdfParserModule.class })
public class StructuralElementTypeRecognizerTest {

    @Inject
    private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

    @Inject
    private StructuralElementSearcher structuralElementSearcher;
    @Inject
    private ReferenceSearcher referenceSearcher;
    @Inject
    private LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;
    @Inject
    private PdflatexWrapper pdflatexWrapper;
    @Inject
    private Latex2PDFMapper latex2pdfMapper;
    @Inject
    private ArxmlivProducer arxmlivProducer;

    @Inject
    private GateProcessingFacade gateProcessingFacade;

    private StructuralElement testProofElement;

    private StructuralElement testTableElement;

    private StructuralElement rootElement;

    @Before
    public void init() throws Exception {

	String documentId = "ivm537";
	String paperUrl = String.format("http://mathnet.ru/ivm537/%s",
		documentId);
	Document document = prepareDoc(documentId);
	Graph<StructuralElement, Reference> graph = referenceSearcher
		.retrieveStructuralGraph(document, paperUrl);

	Collection<StructuralElement> elements = graph.getVertices();
	testProofElement = Iterables.find(elements, new IdPredicate(1082));
	rootElement = Iterables.find(elements, new IdPredicate(0));
	// Second document
	documentId = "ivm18";
	paperUrl = String.format("http://mathnet.ru/ivm537/%s", documentId);
	document = prepareDoc(documentId);
	graph = referenceSearcher.retrieveStructuralGraph(document, paperUrl);
	elements = graph.getVertices();
	testTableElement = Iterables.find(elements, new IdPredicate(5499));
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
    public void testRootElementType() {
	MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
		.predict(rootElement);
	Assert.assertEquals(
		"The type of root element does not equal to the expected one.",
		MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT,
		prediction);
    }

    @Test
    public void testPredictProof() {

	MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
		.predict(testProofElement);
	Assert.assertEquals(MocassinOntologyClasses.PROOF, prediction);
    }

    @Test
    public void testPredictTable() {

	MocassinOntologyClasses prediction = getStructuralElementTypeRecognizer()
		.predict(testTableElement);
	Assert.assertEquals(MocassinOntologyClasses.TABLE, prediction);
    }

    public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
	return structuralElementTypeRecognizer;
    }

}
