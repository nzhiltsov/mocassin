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

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;

/**
 * This facade implementation uses an ANNIE controller instance serialized in
 * the .gapp file (its location is configured by the 'gate.loaded.plugin.config'
 * parameter in <strong>nlp-module.properties</strong>).
 * 
 * @author Nikita Zhiltsov
 * 
 */
class AnnieBasedGateProcessingFacade implements GateProcessingFacade {
    @InjectLogger
    private Logger logger;

    private final AnnieControllerProvider<SerialAnalyserController> annieControllerProvider;

    @Inject
    AnnieBasedGateProcessingFacade(
	    AnnieControllerProvider<SerialAnalyserController> annieControllerProvider) {
	this.annieControllerProvider = annieControllerProvider;
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

    private Document executeProcessor(String arxivId, Document document)
	    throws ProcessException {
	try {

	    SerialAnalyserController annieController = annieControllerProvider
		    .get();
	    Corpus corpus = Factory.newCorpus("temp");
	    corpus.add(document);
	    annieController.setCorpus(corpus);
	    try {
		annieController.execute();
	    } finally {
		Factory.deleteResource(annieController);
		Factory.deleteResource(corpus);
	    }
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
