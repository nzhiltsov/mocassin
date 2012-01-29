package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

class PdflatexWrapperImpl extends AbstractUnixCommandWrapper implements
		PdflatexWrapper {
	private final String PATCHED_LATEX_DIR;

	private final String SHADED_LATEX_DIR;

	private final String PDF_DIR;

	private final String AUX_PDF_DIR;

	@Inject
	PdflatexWrapperImpl(Logger logger,
			@Named("auxiliary.pdf.document.dir") String auxPdfPath,
			@Named("patched.tex.document.dir") String patchedLatexDir,
			@Named("shaded.tex.document.dir") String shadedLatexDir,
			@Named("pdf.document.dir") String pdfDir) {
		super(logger, 6);
		PATCHED_LATEX_DIR = patchedLatexDir;
		SHADED_LATEX_DIR = shadedLatexDir;
		AUX_PDF_DIR = auxPdfPath;
		PDF_DIR = pdfDir;
		this.cmdArray[0] = "pdflatex";
		this.cmdArray[1] = "-interaction";
		this.cmdArray[2] = "nonstopmode";
		this.cmdArray[3] = "-output-directory";
	}

	@Override
	public void compileShaded(String arxivId, int structuralElementId)
			throws PdflatexCompilationException {
		this.cmdArray[4] = PDF_DIR;
		this.cmdArray[5] = String.format("%s/%s", SHADED_LATEX_DIR, StringUtil
				.segmentid2filename(arxivId, structuralElementId, "tex"));
		executeCommands(arxivId);
	}

	@Override
	public void compilePatched(String arxivId)
			throws PdflatexCompilationException {
		this.cmdArray[4] = AUX_PDF_DIR;
		this.cmdArray[5] = String.format("%s/%s", PATCHED_LATEX_DIR,
				StringUtil.arxivid2filename(arxivId, "tex"));
		executeCommands(arxivId);
	}

	private void executeCommands(String arxivId)
			throws PdflatexCompilationException {
		try {
			execute();
			execute(); // double calling is necessary for right cross-references
		} catch (Exception e) {
			String message = String
					.format("failed to compile the PDF document for an arXiv identifier='%s' due to %s",
							arxivId, e.getCause());
			logger.log(Level.SEVERE, message);
			throw new PdflatexCompilationException(message);
		}
	}

}
