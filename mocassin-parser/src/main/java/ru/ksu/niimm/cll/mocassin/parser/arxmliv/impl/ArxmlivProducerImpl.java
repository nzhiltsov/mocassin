package ru.ksu.niimm.cll.mocassin.parser.arxmliv.impl;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ArxmlivProducerImpl extends AbstractUnixCommandWrapper implements
		ArxmlivProducer {

	private final String ARXMLIV_DOCUMENT_DIR;

	private final String LATEX_DIR;

	@Inject
	public ArxmlivProducerImpl(Logger logger,
			@Named("arxmliv.document.dir") String arxmlivDocumentsDir,
			@Named("arxmliv.path") String arxmlivPath,
			@Named("patched.tex.document.dir") String latexDir) {
		super(logger, 4);
		this.ARXMLIV_DOCUMENT_DIR = arxmlivDocumentsDir;
		this.LATEX_DIR = latexDir;
		this.cmdArray[0] = "latexml";
		this.cmdArray[1] = String.format("--path=%s/sty/", arxmlivPath);
	}

	@Override
	public String produce(String arxivId) {
		String arxmlivDocFilePath = String.format("%s/%s",
				ARXMLIV_DOCUMENT_DIR,
				StringUtil.arxivid2filename(arxivId, "tex.xml"));
		this.cmdArray[2] = String
				.format("--destination=%s", arxmlivDocFilePath);
		this.cmdArray[3] = String.format("%s/%s", LATEX_DIR,
				StringUtil.arxivid2filename(arxivId, "tex"));
		try {
			execute();
			return arxmlivDocFilePath;
		} catch (TimeoutException e) {
			String message = String
					.format("failed to produce the arxmliv document for an arXiv identifier='%s' due to timeout",
							arxivId);
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		} catch (Exception e) {
			String message = String
					.format("failed to produce the arxmliv document for an arXiv identifier='%s' due to: %s",
							arxivId, e.getMessage());
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}
	}
}
