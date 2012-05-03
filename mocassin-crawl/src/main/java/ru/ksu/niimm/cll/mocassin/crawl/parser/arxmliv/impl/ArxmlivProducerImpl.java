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
package ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.impl;

import java.io.File;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import expectj.TimeoutException;

public class ArxmlivProducerImpl extends AbstractUnixCommandWrapper implements
	ArxmlivProducer {

    private static final String ARXMLIV_DESTINATION_PARAMETER = "--destination=%s";

    private static final String ARXMLIV_STY_PATH_PARAMETER = "--path=%s/sty/";

    private static final String LATEXML_PATH = "/usr/local/bin/latexml";

    @InjectLogger
    private Logger logger;

    private final String ARXMLIV_DOCUMENT_DIR;

    private final String LATEX_DIR;

    private final String ARXMLIV_PATH;

    @Inject
    public ArxmlivProducerImpl(
	    @Named("arxmliv.document.dir") String arxmlivDocumentsDir,
	    @Named("arxmliv.path") String arxmlivPath,
	    @Named("patched.tex.document.dir") String latexDir) {
	this.ARXMLIV_DOCUMENT_DIR = arxmlivDocumentsDir;
	this.LATEX_DIR = latexDir;
	this.ARXMLIV_PATH = arxmlivPath;
    }

    @Override
    public String getArxmlivDocumentDirectory() {
	return ARXMLIV_DOCUMENT_DIR;
    }

    @Override
    public String produce(String arxivId) {
	String arxmlivDocFilePath = String.format("%s/%s",
		ARXMLIV_DOCUMENT_DIR,
		StringUtil.arxivid2filename(arxivId, "tex.xml"));
	String filename = String.format("%s/%s", LATEX_DIR,
		StringUtil.arxivid2filename(arxivId, "tex"));
	File file = new File(filename);
	if (!file.exists()) {
	    throw new IllegalArgumentException(
		    "Failed to produce an arXMLiv representation for a Latex document, because it does not exist.");
	}
	final String[] cmdArray = new String[4];
	cmdArray[0] = LATEXML_PATH;
	cmdArray[1] = String.format(ARXMLIV_STY_PATH_PARAMETER, ARXMLIV_PATH);
	cmdArray[2] = String.format(ARXMLIV_DESTINATION_PARAMETER,
		arxmlivDocFilePath);
	cmdArray[3] = filename;
	try {
	    execute(cmdArray, null);
	    return arxmlivDocFilePath;
	} catch (TimeoutException e) {
	    logger.error(
		    "Failed to produce an arxmliv document with an identifier='{}' due to timeout",
		    arxivId, e);
	    throw new RuntimeException(e);
	} catch (Exception e) {
	    logger.error(
		    "Failed to produce an arxmliv document with an identifier='{}'",
		    arxivId, e);
	    throw new RuntimeException(e);
	}
    }
}
