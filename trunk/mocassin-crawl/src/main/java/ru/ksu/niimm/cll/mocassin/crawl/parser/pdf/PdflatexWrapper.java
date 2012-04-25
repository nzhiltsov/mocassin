package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

public interface PdflatexWrapper {
	/**
	 * runs the 'pdflatex' command for the <b>patched</b> Latex source of a
	 * paper with a given id
	 * 
	 * @param arxivId
	 * @throws PdflatexCompilationException
	 */
	void compilePatched(String arxivId) throws PdflatexCompilationException;

	/**
	 * runs the 'pdflatex' command for the <b>shaded</b> Latex source of a paper
	 * with a given id
	 * 
	 * @param arxivId
	 * @throws PdflatexCompilationException
	 */
	void compileShaded(String arxivId, int structuralElementId) throws PdflatexCompilationException;
}
