package ru.ksu.niimm.cll.mocassin.parser.pdf;

public interface PdflatexWrapper {
	/**
	 * runs the 'pdflatex' command for the Latex source of a paper with a given
	 * id
	 * 
	 * @param arxivId
	 * @throws PdflatexCompilationException 
	 */
	void compile(String arxivId) throws PdflatexCompilationException;
}
