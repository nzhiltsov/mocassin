package ru.ksu.niimm.cll.mocassin.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.parser.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class LatexDocumentDAOImpl implements LatexDocumentDAO {
	@Inject
	Logger logger;

	private final Parser parser;

	private final String LATEX_DOCUMENT_DIR;

	@Inject
	public LatexDocumentDAOImpl(Parser parser,
			@Named("tex.document.dir") String texDocumentDir) {
		this.parser = parser;
		LATEX_DOCUMENT_DIR = texDocumentDir;
	}

	@Override
	public LatexDocumentModel load(String documentId) {
		String id = documentId.replace("/", "_");
		LatexDocumentModel model = null;
		try {
			model = parser.parse(documentId,
					new FileInputStream(String.format("%s/%s.tex",
							LATEX_DOCUMENT_DIR, id)), true);

		} catch (FileNotFoundException e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"couldn't load a Latex document with id='%s'; an empty model will be returned",
							documentId));
		}
		return model;
	}

}
