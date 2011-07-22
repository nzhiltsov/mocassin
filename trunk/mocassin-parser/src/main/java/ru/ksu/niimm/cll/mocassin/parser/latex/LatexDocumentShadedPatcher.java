package ru.ksu.niimm.cll.mocassin.parser.latex;

public interface LatexDocumentShadedPatcher {
	/**
	 * patches the Latex source of a document with a given arxiv identifier:
	 * wrap around given starting and ending lines with 'shaded' entries (see
	 * 'framed' package)
	 * 
	 * @param arxivId
	 * @param startLine
	 * @param endLine
	 * @param elementId
	 */
	void patch(String arxivId, int startLine, int endLine, int elementId);
}
