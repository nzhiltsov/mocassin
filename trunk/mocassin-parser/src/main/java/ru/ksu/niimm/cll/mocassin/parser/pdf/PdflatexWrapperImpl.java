package ru.ksu.niimm.cll.mocassin.parser.pdf;

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
			if (!execute())
				throw new RuntimeException("Not normal output.");
			execute(); // double calling is necessary for correct cross-references
		} catch (Exception e) {
			logger.error(
					"Failed to compile the PDF document with an identifier='{}'",
					arxivId, e);
			throw new PdflatexCompilationException(e);
		}
	}
}
