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
		String filename = String.format("%s/%s", LATEX_DIR,
				StringUtil.arxivid2filename(arxivId, "tex"));
		File file = new File(filename);
		if (!file.exists()) {
			throw new IllegalArgumentException(
					"Failed to produce an arXMLiv representation for a Latex document, because it does not exist.");
		}
		this.cmdArray[2] = String
				.format("--destination=%s", arxmlivDocFilePath);
		this.cmdArray[3] = filename;
		try {
			execute();
			File outputFile = new File(arxmlivDocFilePath);
			if (!outputFile.exists()) {
				throw new RuntimeException(
						"The output file does not exist. Perhaps, the arxmliv script failed.");
			}
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
