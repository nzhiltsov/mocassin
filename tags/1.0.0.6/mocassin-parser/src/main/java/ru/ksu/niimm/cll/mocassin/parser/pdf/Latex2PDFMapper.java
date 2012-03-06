package ru.ksu.niimm.cll.mocassin.parser.pdf;

public interface Latex2PDFMapper {
	/**
	 * returns the page number in the generated PDF file with a given document
	 * id, on which a given a line is located
	 * 
	 * @param latexLineNumber
	 * @param arxivId
	 *            e.g. "solv-int/9812015"
	 * @return
	 */
	int getPDFPageNumber(int latexLineNumber, String arxivId);

	/**
	 * generates an internal summary representation that maps Latex to PDF
	 * coordinates for a document with a given id
	 * 
	 * @param arxivId
	 * @throws GeneratePdfSummaryException 
	 */
	void generateSummary(String arxivId) throws GeneratePdfSummaryException;
}
