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
package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

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
