package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * TODO: the current bash script contains a bug: if the endLine is commented in
 * the Latex source, ending 'shaded' entry is misplaced
 */
class SedBasedShadedPatcher extends AbstractUnixCommandWrapper implements
		LatexDocumentShadedPatcher {
	@InjectLogger
	private Logger logger;

	private final String PATCHED_LATEX_DIR;

	@Inject
	SedBasedShadedPatcher(@Named("shaded.patcher.script.path") String bashPath,
			@Named("shaded.tex.document.dir") String outputDir,
			@Named("patched.tex.document.dir") String patchedLatexDir) {
		super(6);
		this.PATCHED_LATEX_DIR = patchedLatexDir;
		this.cmdArray[0] = bashPath;
		this.cmdArray[5] = outputDir;
	}

	@Override
	public void patch(String arxivId, int startLine, int endLine, int elementId) {
		this.cmdArray[1] = String.valueOf(startLine);
		this.cmdArray[2] = String.valueOf(endLine);
		this.cmdArray[3] = String.valueOf(elementId);
		this.cmdArray[4] = String.format("%s/%s", PATCHED_LATEX_DIR,
				StringUtil.arxivid2filename(arxivId, "tex"));
		try {
			execute();
		} catch (Exception e) {
			logger.error(
					"Failed to patch the latex source of a document='{}' with shaded entries.",
					arxivId, e);
			throw new RuntimeException(e);
		}

	}
}
