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
package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import java.io.InputStream;


public interface LatexDocumentDAO {
	/**
	 * load the document model for a given id (e.g. <i>math/0410002</i>)
	 * 
	 * @param documentId
	 * @return
	 */
	LatexDocumentModel load(String documentId);

	/**
	 * persists the Latex source which corresponds to a arXiv paper with a given
	 * identifier
	 * 
	 * @param arxivId
	 * @param inputStream
	 */
	void save(String arxivId, InputStream inputStream, String encoding);
}
