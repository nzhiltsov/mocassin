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
package ru.ksu.niimm.cll.mocassin.crawl;

import static java.lang.String.format;

import java.util.Collection;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceStatementExporter;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfHighlighter;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResourceFacade;

import com.google.inject.Inject;

public abstract class AbstractDomainAdapter implements DomainAdapter {

    @Inject
    protected OntologyResourceFacade ontologyResourceFacade;
    @Inject
    protected ReferenceSearcher referenceSearcher;
    @Inject
    protected ReferenceStatementExporter referenceStatementGenerator;
    @Inject
    protected LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;
    @Inject
    protected LatexDocumentDAO latexDocumentDAO;
    @Inject
    protected ArxmlivProducer arxmlivProducer;
    @Inject
    protected GateProcessingFacade gateProcessingFacade;
    @Inject
    protected PdflatexWrapper pdflatexWrapper;
    @Inject
    protected Latex2PDFMapper latex2pdfMapper;
    @Inject
    protected PdfHighlighter pdfHighlighter;

    protected abstract Logger getLogger();

    protected void generateHighlightedPdfs(String arxivId,
	    Collection<StructuralElement> structuralElements) {
	for (StructuralElement element : structuralElements) {
	    int latexStartLine = element.getLatexStartLine();
	    int latexEndLine = element.getLatexEndLine();
	    if (latexStartLine != 0 && latexEndLine != 0) {
		try {
		    pdfHighlighter.generateHighlightedPdf(arxivId,
			    element.getId(), latexStartLine, latexEndLine);
		} catch (Exception e) {
		    getLogger()
			    .error("Failed to generate the highlighted PDF for a segment with id = {}",
				    format("%s/%d", arxivId, element.getId()),
				    e);
		}
	    } else {
		getLogger()
			.warn("Skipped generating the highlighted PDF for a segment with id = {}. Perhaps, the segment location is incorrect.",
				format("%s/%d", arxivId, element.getId()));
	    }
	}
    }

}
