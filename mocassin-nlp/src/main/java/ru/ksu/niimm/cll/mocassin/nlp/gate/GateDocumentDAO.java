package ru.ksu.niimm.cll.mocassin.nlp.gate;

import gate.Document;
import gate.util.GateException;

import java.util.List;

public interface GateDocumentDAO {
	/**
	 * returns identifiers of all the documents from given corpus (see
	 * parameters in a configuration file)
	 * 
	 * @return
	 * @throws GateException
	 */
	List<String> getDocumentIds() throws Exception;

	/**
	 * returns document with given id; <br>
	 * IMPORTANT: call of 'release' method ({@link #release(Document)}) is
	 * required after the work with opened document has finished
	 * 
	 * @param documentId
	 * @return
	 * @throws GateException
	 */
	Document load(String documentId) throws Exception;

	/**
	 * releases resources connected with given document
	 * 
	 * @param document
	 */
	void release(Document document);
}
