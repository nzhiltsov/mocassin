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

class PdflatexWrapperImpl extends AbstractUnixCommandWrapper implements
		PdflatexWrapper {
	private static final String SUCCESS_FLAG = "Output written on";

	@InjectLogger
	private Logger logger;

	private final String PATCHED_LATEX_DIR;

	private final String SHADED_LATEX_DIR;

	private final String PDF_DIR;

	private final String AUX_PDF_DIR;

	@Inject
	PdflatexWrapperImpl(@Named("auxiliary.pdf.document.dir") String auxPdfPath,
			@Named("patched.tex.document.dir") String patchedLatexDir,
			@Named("shaded.tex.document.dir") String shadedLatexDir,
			@Named("pdf.document.dir") String pdfDir) {
		super(6, SUCCESS_FLAG);
		PATCHED_LATEX_DIR = patchedLatexDir;
		SHADED_LATEX_DIR = shadedLatexDir;
		AUX_PDF_DIR = auxPdfPath;
		PDF_DIR = pdfDir;
		setCmdArray(0, "pdflatex");
		setCmdArray(1, "-interaction");
		setCmdArray(2, "nonstopmode");
		setCmdArray(3, "-output-directory");
	}

	@Override
	public void compileShaded(String arxivId, int structuralElementId)
			throws PdflatexCompilationException {
		setCmdArray(4, PDF_DIR);
		setCmdArray(5, String.format("%s/%s", SHADED_LATEX_DIR, StringUtil
				.segmentid2filename(arxivId, structuralElementId, "tex")));
		executeCommands(arxivId);
	}

	@Override
	public void compilePatched(String arxivId)
			throws PdflatexCompilationException {
		setCmdArray(4, AUX_PDF_DIR);
		setCmdArray(
				5,
				String.format("%s/%s", PATCHED_LATEX_DIR,
						StringUtil.arxivid2filename(arxivId, "tex")));
		executeCommands(arxivId);
	}

	private void executeCommands(String arxivId)
			throws PdflatexCompilationException {
		try {
			if (!execute())
				throw new RuntimeException(
						"Not normal output while compiling PDF");
			execute(); // double calling is necessary for correct
						// cross-references
		} catch (Exception e) {
			logger.error(
					"Failed to compile the PDF document with an identifier='{}'",
					arxivId, e);
			throw new PdflatexCompilationException(e);
		}
	}
}
