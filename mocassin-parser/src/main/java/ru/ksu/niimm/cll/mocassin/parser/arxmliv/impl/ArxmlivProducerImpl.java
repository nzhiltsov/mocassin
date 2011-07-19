package ru.ksu.niimm.cll.mocassin.parser.arxmliv.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ArxmlivProducerImpl implements ArxmlivProducer {
	@Inject
	private Logger logger;

	private final String[] cmdArray;

	private final String ARXMLIV_DOCUMENT_DIR;

	private final String LATEX_DIR;

	@Inject
	public ArxmlivProducerImpl(
			@Named("arxmliv.document.dir") String arxmlivDocumentsDir,
			@Named("arxmliv.path") String arxmlivPath,
			@Named("patched.tex.document.dir") String latexDir) {
		this.ARXMLIV_DOCUMENT_DIR = arxmlivDocumentsDir;
		this.LATEX_DIR = latexDir;
		this.cmdArray = new String[4];
		this.cmdArray[0] = "latexml";
		this.cmdArray[1] = String.format("--path=%s/sty", arxmlivPath);
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
			Process process = Runtime.getRuntime().exec(cmdArray);
			if (process.waitFor() == 0) {
				return arxmlivDocFilePath;
			} else {
				InputStream errorStream = process.getErrorStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(errorStream, writer, "utf8");
				logger.log(Level.SEVERE, writer.toString());
				throw new Exception("process termination hasn't been normal");
			}
		} catch (Exception e) {
			String message = String
					.format("failed to produce the arxmliv document for arXiv identifier='%s'",
							arxivId);
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}
	}
}
