package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import java.io.File;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

class SedBasedHeaderPatcher extends AbstractUnixCommandWrapper implements
		LatexDocumentHeaderPatcher {
	@InjectLogger
	private Logger logger;

	private final String texDocumentDir;

	@Inject
	SedBasedHeaderPatcher(
			@Named("header.patcher.script.path") String patcherScriptPath,
			@Named("bash.path") String bashPath,
			@Named("patched.tex.document.dir") String outputDir,
			@Named("tex.document.dir") String texDocumentDir) {
		super(4);
		setCmdArray(0, bashPath);
		setCmdArray(1, patcherScriptPath);
		setCmdArray(2, outputDir);
		this.texDocumentDir = texDocumentDir;
	}

	@Override
	public void patch(String arxivId) {

		String filename = String.format("%s/%s", texDocumentDir,
				StringUtil.arxivid2filename(arxivId, "tex"));
		File file = new File(filename);
		if (!file.exists()) {
			throw new IllegalArgumentException(
					"Failed to patch a Latex document, because it does not exist.");
		}
		setCmdArray(3, filename);
		try {
			execute();
		} catch (Exception e) {
			logger.error("Failed to patch the latex source of a document='{}'",
					arxivId, e);
			throw new RuntimeException(e);
		}

	}
}
