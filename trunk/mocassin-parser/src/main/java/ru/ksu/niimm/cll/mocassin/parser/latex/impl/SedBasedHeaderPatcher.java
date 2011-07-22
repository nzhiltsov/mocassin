package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SedBasedHeaderPatcher extends AbstractUnixCommandWrapper implements
		LatexDocumentHeaderPatcher {

	private final String texDocumentDir;

	@Inject
	public SedBasedHeaderPatcher(Logger logger,
			@Named("header.patcher.script.path") String patcherScriptPath,
			@Named("bash.path") String bashPath,
			@Named("patched.tex.document.dir") String outputDir,
			@Named("tex.document.dir") String texDocumentDir) {
		super(logger, 4);
		this.cmdArray[0] = bashPath;
		this.cmdArray[1] = patcherScriptPath;
		this.cmdArray[2] = outputDir;
		this.texDocumentDir = texDocumentDir;
	}

	@Override
	public void patch(String arxivId) {

		this.cmdArray[3] = String.format("%s/%s", texDocumentDir,
				StringUtil.arxivid2filename(arxivId, "tex"));
		try {
			execute();
		} catch (Exception e) {
			String message = String.format(
					"failed to patch the latex source of a document='%s'",
					arxivId);
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}

	}
}
