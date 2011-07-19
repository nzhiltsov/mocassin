package ru.ksu.niimm.cll.mocassin.parser.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SedCommandPatcher implements LatexDocumentHeaderPatcher {
	@Inject
	private Logger logger;

	private final String[] cmdArray;

	private final String texDocumentDir;

	@Inject
	public SedCommandPatcher(
			@Named("header.patcher.script.path") String patcherScriptPath,
			@Named("bash.path") String bashPath,
			@Named("patched.tex.document.dir") String outputDir,
			@Named("tex.document.dir") String texDocumentDir) {
		this.cmdArray = new String[4];
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
			Process process = Runtime.getRuntime().exec(cmdArray);
			if (process.waitFor() == 0) {
				return;
			} else {
				InputStream errorStream = process.getErrorStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(errorStream, writer, "utf8");
				logger.log(Level.SEVERE, writer.toString());
				throw new Exception("process termination hasn't been normal");
			}
		} catch (Exception e) {
			String message = String.format(
					"failed to patch the latex source of a document='%s'",
					arxivId);
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}

	}
}
