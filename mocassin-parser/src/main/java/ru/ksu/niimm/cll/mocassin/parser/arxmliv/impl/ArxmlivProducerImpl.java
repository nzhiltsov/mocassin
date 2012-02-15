package ru.ksu.niimm.cll.mocassin.parser.arxmliv.impl;

import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ArxmlivProducerImpl extends AbstractUnixCommandWrapper implements
		ArxmlivProducer {
	@InjectLogger
	private Logger logger;

	private final String ARXMLIV_DOCUMENT_DIR;

	private final String LATEX_DIR;

	@Inject
	public ArxmlivProducerImpl(
			@Named("arxmliv.document.dir") String arxmlivDocumentsDir,
			@Named("arxmliv.path") String arxmlivPath,
			@Named("patched.tex.document.dir") String latexDir) {
		super(4);
		this.ARXMLIV_DOCUMENT_DIR = arxmlivDocumentsDir;
		this.LATEX_DIR = latexDir;
		this.cmdArray[0] = "/usr/local/bin/latexml";
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
