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

/**
 * This patcher adds necessary package declarations to the Latex source file
 * header with a given document identifier.
 * 
 * <p>The location of source files is configured in
 * <strong>'parser-module.properties'</strong> file.
 * 
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface LatexDocumentHeaderPatcher {
    /**
     * Patches the Latex source file for a document with a given id.
     * 
     * @param documentId
     *            document id, e.g. 'ivm18'
     */
    void patch(String documentId);
}
