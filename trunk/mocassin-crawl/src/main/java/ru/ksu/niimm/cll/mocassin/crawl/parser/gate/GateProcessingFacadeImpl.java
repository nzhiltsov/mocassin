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

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.persist.PersistenceException;
import gate.security.SecurityException;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;

class GateProcessingFacadeImpl implements GateProcessingFacade {
    @InjectLogger
    private Logger logger;

    private final AnnieControllerProvider<SerialAnalyserController> annieControllerProvider;

    private final GateDocumentDAO gateDocumentDAO;

    @Inject
    GateProcessingFacadeImpl(
	    AnnieControllerProvider<SerialAnalyserController> annieControllerProvider,
	    GateDocumentDAO gateDocumentDAO) {
	this.annieControllerProvider = annieControllerProvider;
	this.gateDocumentDAO = gateDocumentDAO;
    }

    @Override
    public Document process(String paperId, File file, String encoding) {
	try {
	    Document document = Factory.newDocument(file.toURI().toURL(),
		    encoding);
	    return executeProcessor(paperId, document);
	} catch (Throwable e) {
	    logger.error("Failed to process a document with id = {}.", e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public Document process(String paperId, InputStream inputStream,
	    String encoding) {
	try {
	    Writer sw = new StringWriter();
	    IOUtils.copy(inputStream, sw, encoding);
	    Document document = Factory.newDocument(sw.toString());
	    return executeProcessor(paperId, document);
	} catch (Throwable e) {
	    logger.error("Failed to process a document with id = {}.", e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public void process(String paperId) throws AccessGateDocumentException,
	    AccessGateStorageException, ProcessException {
	Document document = gateDocumentDAO.load(StringUtil
		.arxivid2gateid(paperId));
	try {
	    executeProcessor(paperId, document);
	    document.getDataStore().sync(document);
	} catch (PersistenceException e) {
	    logger.error("Failed to update the document with id='{}'", paperId,
		    e);
	    throw new ProcessException(e);
	} catch (SecurityException e) {
	    logger.error("Failed to update the document with id='{}'", paperId,
		    e);
	    throw new ProcessException(e);
	} finally {
	    gateDocumentDAO.release(document);
	}

    }

    private Document executeProcessor(String arxivId, Document document)
	    throws ProcessException {
	try {
	    SerialAnalyserController annieController = annieControllerProvider
		    .get();
	    Corpus corpus = Factory.newCorpus("temp");
	    corpus.add(document);
	    annieController.setCorpus(corpus);
	    annieController.execute();
	    return document;
	} catch (ExecutionException e) {
	    logger.error(
		    "Failed to execute ANNIE controller over a document with id='{}'",
		    arxivId, e);
	    throw new ProcessException(e);
	} catch (AnnieControllerCreationException e) {
	    logger.error("Failed to create the ANNIE controller", e);
	    throw new ProcessException(e);
	} catch (ResourceInstantiationException e) {
	    logger.error("Failed to create a temporary corpus", e);
	    throw new ProcessException(e);
	}
    }
}
