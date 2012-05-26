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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import gate.Document;
import gate.Factory;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.ArxmlivProducer;
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

    private final String SENTENCE_ANNOTATION_NAME = "Sentence";

    @Before
    public void init() throws Exception {
	firstPath = prepareDoc(FIRST_DOC_ID);
	secondPath = prepareDoc(SECOND_DOC_ID);
    }

    @Test
    public void testProcess() throws AccessGateDocumentException,
	    AccessGateStorageException, ProcessException {
	Document firstDoc = gateProcessingFacade.process(FIRST_DOC_ID,
		new File(firstPath), "utf8");
	try {
	    Assert.assertTrue(
		    "Sentence set is empty.",
		    firstDoc.getAnnotations(
			    GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
			    .get(SENTENCE_ANNOTATION_NAME).size() > 0);
	} finally {
	    if (firstDoc != null) {
		Factory.deleteResource(firstDoc);
	    }
	}
	Document secondDoc = gateProcessingFacade.process(SECOND_DOC_ID,
		new File(secondPath), "utf8");
	try {
	    Assert.assertTrue(
		    "Sentence set is empty.",
		    secondDoc
			    .getAnnotations(
				    GateFormatConstants.DEFAULT_ANNOTATION_SET_NAME)
			    .get(SENTENCE_ANNOTATION_NAME).size() > 0);
	} finally {
	    if (secondDoc != null) {
		Factory.deleteResource(secondDoc);
	    }
	}
    }

    private String prepareDoc(String documentId)
	    throws PdflatexCompilationException, GeneratePdfSummaryException {
	latexDocumentHeaderPatcher.patch(documentId);
	pdflatexWrapper.compilePatched(documentId);
	latex2pdfMapper.generateSummary(documentId);
	return arxmlivProducer.produce(documentId);

    }
}
