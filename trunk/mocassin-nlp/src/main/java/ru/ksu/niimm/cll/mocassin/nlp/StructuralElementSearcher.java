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
	List<StructuralElement> retrieve(Document document);
}
