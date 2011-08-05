package ru.ksu.niimm.cll.mocassin.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.Parser;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class LatexDocumentDAOImpl implements LatexDocumentDAO {
	@Inject
	Logger logger;

	private final Parser parser;

	private final String LATEX_DOCUMENT_DIR;
	
	private final String PATCHED_LATEX_DOCUMENT_DIR;

	@Inject
	public LatexDocumentDAOImpl(Parser parser,
			@Named("patched.tex.document.dir") String patchedTexDocumentDir, @Named("tex.document.dir") String texDocumentDir) {
		this.parser = parser;
		this.PATCHED_LATEX_DOCUMENT_DIR = patchedTexDocumentDir;
		LATEX_DOCUMENT_DIR = texDocumentDir;
	}

	@Override
	public LatexDocumentModel load(String documentId) {
		String filename = StringUtil.arxivid2filename(documentId, "tex");
		LatexDocumentModel model = null;
		try {
			model = parser.parse(
					documentId,
					new FileInputStream(String.format("%s/%s",
							PATCHED_LATEX_DOCUMENT_DIR, filename)), true);

		} catch (FileNotFoundException e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"couldn't load a Latex document with id='%s'; an empty model will be returned",
							documentId));
		}
		return model;
	}

	@Override
	public void save(String arxivId, InputStream inputStream, String encoding) {
		String filename = StringUtil.arxivid2filename(arxivId, "tex");
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(String.format("%s/%s",
					LATEX_DOCUMENT_DIR, filename)), encoding);
			IOUtils.copy(inputStream, writer, encoding);
			writer.flush();
			writer.close();
			inputStream.close();
		} catch (IOException e) {
			String message = String.format(
					"failed to save the Latex source with id='%s' due to: %s",
					arxivId, e.getMessage());
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}

	}
}
