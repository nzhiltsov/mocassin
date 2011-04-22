package ru.ksu.niimm.cll.mocassin.nlp.recognizer;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

public interface StructuralElementTypeRecognizer {
	

	/**
	 * predicts which type given element has
	 * 
	 * @param structuralElement
	 * @return
	 */
	MocassinOntologyClasses predict(StructuralElement structuralElement);

}
