package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

public interface PdfHighlighter {
	/**
	 * generate highlighted PDF for a segment with given data
	 * 
	 * @param structuralElementUri
	 * @param latexStartLine
	 * @param latexEndLine
	 * @throws PdflatexCompilationException 
	 */
	void generateHighlightedPdf(String arxivId, int structuralElementId,
			int latexStartLine, int latexEndLine) throws PdflatexCompilationException;
}
