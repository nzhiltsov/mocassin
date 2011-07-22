package ru.ksu.niimm.cll.mocassin.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import ru.ksu.niimm.cll.mocassin.parser.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class LatexDocumentDAOImpl implements LatexDocumentDAO {
	@Inject
	Logger logger;

	private final Parser parser;

	private final String LATEX_DOCUMENT_DIR;

	@Inject
	public LatexDocumentDAOImpl(Parser parser,
			@Named("patched.tex.document.dir") String texDocumentDir) {
		this.parser = parser;
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
							LATEX_DOCUMENT_DIR, filename)), true);

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
	public void save(String arxivId, InputStream inputStream) {
		String filename = StringUtil.arxivid2filename(arxivId, "tex");
		try {
			FileWriter writer = new FileWriter(String.format("%s/%s",
					LATEX_DOCUMENT_DIR, filename));
			IOUtils.copy(inputStream, writer, "utf8");
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
