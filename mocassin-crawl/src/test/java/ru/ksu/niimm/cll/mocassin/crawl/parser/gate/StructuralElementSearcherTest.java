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

import static java.lang.String.format;
import gate.Document;
import gate.Factory;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.StructuralElementImpl.IdComparator;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.GeneratePdfSummaryException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class, LatexParserModule.class,
	PdfParserModule.class })
public class StructuralElementSearcherTest {

    private static final String DOC_ID = "ivm18";
    private static final String DOC_URL = String.format("http://mathnet.ru/%s",
	    DOC_ID);

    @Inject
    private StructuralElementSearcher structuralElementSearcher;

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

    private Document document;
    /**
     * values should be added according to ascending order of the ids
     */
    private static Map<Integer, Integer> id2pagenumber = new ImmutableMap.Builder<Integer, Integer>()
	    .put(19, 1).put(2900, 4).build();

    @Before
    public void init() throws Exception {
	document = prepareDoc(DOC_ID);
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
    public void testRetrieveElements() {
	List<StructuralElement> elements = getStructuralElementSearcher()
		.retrieveElements(document, DOC_URL);
	Collections.sort(elements, new IdComparator());
	Iterator<StructuralElement> iterator = elements.iterator();
	int checkedNumber = 0;
	while (iterator.hasNext()) {
	    StructuralElement element = iterator.next();
	    for (Map.Entry<Integer, Integer> entry : id2pagenumber.entrySet()) {

		if (element.getId() == entry.getKey()) {
		    Assert.assertEquals(
			    format("The start page of the element='%s' does not equal to the expected one",
				    element.getUri()), entry.getValue()
				    .intValue(), element.getStartPageNumber());
		    checkedNumber++;
		}
	    }
	}
	Assert.assertEquals(
		"Number of checked elements does not equal to the expected one",
		id2pagenumber.entrySet().size(), checkedNumber);

    }

    public StructuralElementSearcher getStructuralElementSearcher() {
	return structuralElementSearcher;
    }

    @After
    public void after() {
	Factory.deleteResource(document);
    }
}
