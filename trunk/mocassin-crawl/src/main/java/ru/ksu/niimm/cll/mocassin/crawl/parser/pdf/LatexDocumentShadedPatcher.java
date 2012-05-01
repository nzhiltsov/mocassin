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
