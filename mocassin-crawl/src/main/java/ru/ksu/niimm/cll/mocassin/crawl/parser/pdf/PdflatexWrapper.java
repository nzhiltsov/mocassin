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
