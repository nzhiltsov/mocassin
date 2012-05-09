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
 * This facade handles read/write operations with temporary Latex source files.
 * 
 * <p>The location of saved files is configured in the
 * <strong>'parser-module.properties'</strong> file.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface LatexDocumentDAO {
    /**
     * Loads the document model for a given id
     * 
     * @param documentId
     *            document id, e.g. <i>'math/0410002'</i>
     * @returns latex document model
     */
    LatexDocumentModel load(String documentId);

    /**
     * Persists the Latex source in a given stream, which corresponds to a
     * document with a given identifier
     * 
     * @param documentId
     *            document id, e.g. <i>'math/0410002'</i>
     * @param inputStream
     *            input stream
     * @param encoding
     *            encoding to parse the stream
     * 
     */
    void save(String documentId, InputStream inputStream, String encoding);
}
