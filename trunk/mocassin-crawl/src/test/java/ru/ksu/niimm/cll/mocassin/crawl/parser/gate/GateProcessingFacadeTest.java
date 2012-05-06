package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.GeneratePdfSummaryException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class, PdfParserModule.class,
	LatexParserModule.class })
public class GateProcessingFacadeTest {
    private static final String SECOND_DOC_ID = "ivm537";
    private static final String FIRST_DOC_ID = "ivm18";
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
    private String firstPath;
    private String secondPath;

    @Before
    public void init() throws Exception {
	firstPath = prepareDoc(FIRST_DOC_ID);
	secondPath = prepareDoc(SECOND_DOC_ID);
    }

    @Test
    public void testProcess() throws AccessGateDocumentException,
	    AccessGateStorageException, ProcessException {
	gateProcessingFacade.process(FIRST_DOC_ID, new File(firstPath), "utf8");
	gateProcessingFacade.process(SECOND_DOC_ID, new File(secondPath),
		"utf8");
    }

    private String prepareDoc(String documentId)
	    throws PdflatexCompilationException, GeneratePdfSummaryException {
	latexDocumentHeaderPatcher.patch(documentId);
	pdflatexWrapper.compilePatched(documentId);
	latex2pdfMapper.generateSummary(documentId);
	return arxmlivProducer.produce(documentId);

    }
}
