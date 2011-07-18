package ru.ksu.niimm.cll.mocassin.parser;

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;

public interface LatexDocumentDAO {
	/**
	 * load the document model for a given id (e.g. <i>math/0410002</i>)
	 * 
	 * @param documentId
	 * @return
	 */
	LatexDocumentModel load(String documentId);

	/**
	 * persists the Latex source which corresponds to a arXiv paper with a given
	 * identifier
	 * 
	 * @param arxivId
	 * @param inputStream
	 */
	void save(String arxivId, InputStream inputStream);
}
