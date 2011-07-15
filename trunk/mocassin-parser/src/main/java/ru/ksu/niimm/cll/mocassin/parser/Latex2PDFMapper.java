package ru.ksu.niimm.cll.mocassin.parser;

public interface Latex2PDFMapper {
	/**
	 * returns the page number in the generated PDF file with a given document id, on which a given a line
	 * is located
	 * 
	 * @param latexLineNumber
	 * @param documentId e.g. "solv-int/9812015"
	 * @return
	 */
	int getPDFPageNumber(int latexLineNumber, String documentId);
}
