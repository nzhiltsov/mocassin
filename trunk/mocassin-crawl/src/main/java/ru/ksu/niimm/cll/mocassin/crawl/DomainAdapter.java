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
package ru.ksu.niimm.cll.mocassin.crawl;

/**
 * 
 * Domain adapter encapsulates processing facilities targeted on indexing the
 * particular domain.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface DomainAdapter {
    /**
     * This method will be called by the crawler while indexing the domain
     * documents
     * 
     * @param documentId
     *            unique identifier used in the domain, e.g. 'ivm18' on
     *            MathNet.Ru or 'math/0010215' on ArXiv.org
     * @return text contents of the parsed document
     * @throws Exception
     *             if parsing fails
     */
    String handle(String documentId) throws Exception;

}
