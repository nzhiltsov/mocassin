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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

public interface GateProcessingFacade {
	/**
	 * processes a document (stored in the default document store) with a given
	 * arxiv id by the configurable set of GATE plugins
	 * 
	 * @param document
	 * @throws AccessGateStorageException
	 * @throws AccessGateDocumentException
	 * @throws ProcessException 
	 */
	void process(String arxivId) throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException;
}
