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

/**
 * Parser that analyzes a document and builds the its graph model
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface Parser {
    /**
     * Parses a given input stream using a given encoding
     * 
     * @param docId
     *            document id, e.g. 'ivm18'
     * @param inputStream
     *            input stream
     * @param encoding
     *            encoding to parse the stream
     * @param closeStream
     *            boolean, if the stream should be closed
     * @returns an object that represents the structure of a Latex document
     */
    LatexDocumentModel parse(String docId, InputStream inputStream,
	    String encoding, boolean closeStream);
}
