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
package ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv;

public interface ArxmlivProducer {
	/**
	 * produces the <a href="https://trac.kwarc.info/arXMLiv">arXMLiv</a>
	 * representation of a paper with a given arXiv identifier
	 * 
	 * @param arxivId
	 * @return local file path
	 */
	String produce(String arxivId);

	String getArxmlivDocumentDirectory();
}
