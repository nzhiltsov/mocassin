package ru.ksu.niimm.cll.mocassin.nlp.gate;

public interface GateProcessingFacade {
	/**
	 * processes a document (stored in the default document store) with a given
	 * arxiv id by the configurable set of GATE plugins
	 * 
	 * @param document
	 * @throws AccessGateStorageException
	 * @throws AccessGateDocumentException
	 * @throws ProcessException 
	 */
	void process(String arxivId) throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException;
}
