package ru.ksu.niimm.cll.mocassin.nlp;

import gate.Document;

import java.util.List;

/**
 * Goal of the interface to represent a functionality of searching the
 * structural elements in GATE documents
 * 
 * @author nzhiltsov
 * 
 */
public interface StructuralElementSearcher {
	/**
	 * retrieve all the structural element of a given document
	 * 
	 * @param document
	 * @return
	 */
	List<StructuralElement> retrieve(Document document);

	/**
	 * returns element by id for a given document
	 * 
	 * @param document
	 * @param id
	 * @return
	 */
	StructuralElement findById(Document document, int id);
}
