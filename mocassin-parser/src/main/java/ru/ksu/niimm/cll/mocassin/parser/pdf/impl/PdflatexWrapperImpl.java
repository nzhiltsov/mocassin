package ru.ksu.niimm.cll.mocassin.parser.pdf.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.parser.pdf.PdflatexCompilationException;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class PdflatexWrapperImpl extends AbstractUnixCommandWrapper implements
		PdflatexWrapper {
	private final String LATEX_DIR;

	@Inject
	public PdflatexWrapperImpl(Logger logger,
			@Named("auxiliary.pdf.document.dir") String auxPdfPath,
			@Named("patched.tex.document.dir") String latexDir) {
		super(logger, 6);
		LATEX_DIR = latexDir;
		this.cmdArray[0] = "pdflatex";
		this.cmdArray[1] = "-interaction";
		this.cmdArray[2] = "nonstopmode";
		this.cmdArray[3] = "-output-directory";
		this.cmdArray[4] = auxPdfPath;
	}

	@Override
	public void compile(String arxivId) throws PdflatexCompilationException {
		this.cmdArray[5] = String.format("%s/%s", LATEX_DIR,
				StringUtil.arxivid2filename(arxivId, "tex"));
		try {
			execute(arxivId);
			execute(arxivId); // double calling is necessary for right cross-references
		} catch (Exception e) {
			String message = String
					.format("failed to compile the PDF document for an arXiv identifier='%s'",
							arxivId);
			logger.log(Level.SEVERE, message);
			throw new PdflatexCompilationException(message);
		}
	}

}
