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
package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * TODO: the current bash script contains a bug: if the endLine is commented in
 * the Latex source, ending 'shaded' entry is misplaced
 */
class SedBasedShadedPatcher extends AbstractUnixCommandWrapper implements
	LatexDocumentShadedPatcher {
    @InjectLogger
    private Logger logger;

    private final String PATCHED_LATEX_DIR;

    private final String BASH_PATH;

    private final String OUTPUT_DIR;

    @Inject
    SedBasedShadedPatcher(@Named("shaded.patcher.script.path") String bashPath,
	    @Named("shaded.tex.document.dir") String outputDir,
	    @Named("patched.tex.document.dir") String patchedLatexDir) {
	this.PATCHED_LATEX_DIR = patchedLatexDir;
	this.BASH_PATH = bashPath;
	this.OUTPUT_DIR = outputDir;
    }

    @Override
    public void patch(String arxivId, int startLine, int endLine, int elementId) {
	final String[] cmdArray = new String[6];
	cmdArray[0] = BASH_PATH;
	cmdArray[1] = String.valueOf(startLine);
	cmdArray[2] = String.valueOf(endLine);
	cmdArray[3] = String.valueOf(elementId);
	cmdArray[4] = String.format("%s/%s", PATCHED_LATEX_DIR,
		StringUtil.arxivid2filename(arxivId, "tex"));
	cmdArray[5] = OUTPUT_DIR;
	try {
	    execute(cmdArray, null);
	} catch (Exception e) {
	    logger.error(
		    "Failed to patch the latex source of a document='{}' with shaded entries.",
		    arxivId, e);
	    throw new RuntimeException(e);
	}

    }
}
