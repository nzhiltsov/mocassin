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
import java.util.List;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceStatementGenerator;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfHighlighter;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.model.Link;
import ru.ksu.niimm.cll.mocassin.util.model.Link.PdfLinkPredicate;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractDomainAdapter implements DomainAdapter {

	@Inject
	protected OntologyResourceFacade ontologyResourceFacade;
	@Inject
	protected ReferenceSearcher referenceSearcher;
	@Inject
	protected ReferenceStatementGenerator referenceStatementGenerator;
	@Inject
	protected LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;
	@Inject
	protected LatexDocumentDAO latexDocumentDAO;
	@Inject
	protected ArxmlivProducer arxmlivProducer;
	@Inject
	protected GateDocumentDAO gateDocumentDAO;
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

	protected Graph<StructuralElement, Reference> extractStructuralElements(
			ArticleMetadata metadata) {
		ParsedDocument document = getParsedDocument(metadata);
		Graph<StructuralElement, Reference> graph = referenceSearcher
				.retrieveStructuralGraph(document);
		return graph;
	}

	private ParsedDocument getParsedDocument(ArticleMetadata metadata) {
		Link pdfLink = Iterables.find(metadata.getLinks(),
				new PdfLinkPredicate());
		ParsedDocument document = new ParsedDocumentImpl(
				metadata.getCollectionId(), metadata.getId(), pdfLink.getHref());
		return document;
	}

	

}
