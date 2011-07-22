package ru.ksu.niimm.cll.mocassin.parser.pdf.impl;

import com.google.inject.Inject;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentShadedPatcher;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfHighlighter;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdflatexWrapper;

public class PdfHighlighterImpl implements PdfHighlighter {
	private final LatexDocumentShadedPatcher latexDocumentShadedPatcher;

	private final PdflatexWrapper pdflatexWrapper;

	@Inject
	public PdfHighlighterImpl(
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
