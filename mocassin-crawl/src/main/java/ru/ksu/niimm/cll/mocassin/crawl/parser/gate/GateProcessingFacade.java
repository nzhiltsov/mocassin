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

import gate.Document;

import java.io.File;
import java.io.InputStream;

/**
 * This facade processes a given file or stream as a GATE document.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface GateProcessingFacade {
    /**
     * Processes a given file with a given id by the configurable set of GATE
     * plugins
     * 
     * <p>
     * <strong>WARNING</strong>: client code should release resources related to
     * the returned document by itself using {@code Factory.deleteResource()}
     * 
     * @param document
     * @throws AccessGateStorageException
     * @throws AccessGateDocumentException
     * @throws ProcessException
     * @returns document with new annotations
     */
    Document process(String documentId, File file, String encoding);

    /**
     * Processes a given input stream with a given id by the configurable set of
     * GATE plugins
     * 
     * <p>
     * <strong>WARNING</strong>: client code should release resources related to
     * the returned document by itself using {@code Factory.deleteResource()}
     * 
     * @param document
     * @throws AccessGateStorageException
     * @throws AccessGateDocumentException
     * @throws ProcessException
     * @returns document with new annotations
     */
    Document process(String documentId, InputStream inputStream, String encoding);
}
