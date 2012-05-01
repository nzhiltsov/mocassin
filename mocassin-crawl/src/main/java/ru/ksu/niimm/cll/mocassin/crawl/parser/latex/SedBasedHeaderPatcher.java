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

import java.io.File;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

class SedBasedHeaderPatcher extends AbstractUnixCommandWrapper implements
	LatexDocumentHeaderPatcher {
    @InjectLogger
    private Logger logger;

    private final String texDocumentDir;

    private final String BASH_PATH;

    private final String PATCHER_SCRIPT_PATH;

    private final String OUTPUT_DIR;

    @Inject
    SedBasedHeaderPatcher(
	    @Named("header.patcher.script.path") String patcherScriptPath,
	    @Named("bash.path") String bashPath,
	    @Named("patched.tex.document.dir") String outputDir,
	    @Named("tex.document.dir") String texDocumentDir) {
	this.texDocumentDir = texDocumentDir;
	this.BASH_PATH = bashPath;
	this.PATCHER_SCRIPT_PATH = patcherScriptPath;
	this.OUTPUT_DIR = outputDir;
    }

    @Override
    public void patch(String arxivId) {

	String filename = String.format("%s/%s", texDocumentDir,
		StringUtil.arxivid2filename(arxivId, "tex"));
	File file = new File(filename);
	if (!file.exists()) {
	    throw new IllegalArgumentException(
		    "Failed to patch a Latex document, because it does not exist.");
	}
	final String[] cmdArray = new String[4];
	cmdArray[0] = BASH_PATH;
	cmdArray[1] = PATCHER_SCRIPT_PATH;
	cmdArray[2] = OUTPUT_DIR;
	cmdArray[3] = filename;
	try {
	    execute(cmdArray, null);
	} catch (Exception e) {
	    logger.error("Failed to patch the latex source of a document='{}'",
		    arxivId, e);
	    throw new RuntimeException(e);
	}

    }
}
