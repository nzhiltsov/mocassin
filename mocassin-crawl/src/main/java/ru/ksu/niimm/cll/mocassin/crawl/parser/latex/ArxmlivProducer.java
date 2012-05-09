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
 * Wrapper for <a href="https://trac.kwarc.info/arXMLiv">arXMLiv</a> tools.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface ArxmlivProducer {
    /**
     * Produces the XML representation of a document latex source with a given
     * identifier
     * 
     * @param documentId
     *            document id
     * @return XML file local path
     */
    String produce(String documentId);

    /**
     * 
     * @returns local path, where XML files are located. Its value is configured
     *          in the <strong>'parser-module.properties'</strong> file
     */
    String getArxmlivDocumentDirectory();
}
