package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

import com.google.inject.Inject;


class PdfHighlighterImpl implements PdfHighlighter {
	private final LatexDocumentShadedPatcher latexDocumentShadedPatcher;

	private final PdflatexWrapper pdflatexWrapper;

	@Inject
	PdfHighlighterImpl(
			LatexDocumentShadedPatcher latexDocumentShadedPatcher,
			PdflatexWrapper pdflatexWrapper) {
		this.latexDocumentShadedPatcher = latexDocumentShadedPatcher;
		this.pdflatexWrapper = pdflatexWrapper;
	}

	@Override
	public void generateHighlightedPdf(String arxivId, int structuralElementId,
			int latexStartLine, int latexEndLine)
			throws PdflatexCompilationException {
		latexDocumentShadedPatcher.patch(arxivId, latexStartLine, latexEndLine,
				structuralElementId);
		pdflatexWrapper.compileShaded(arxivId, structuralElementId);
	}

}
