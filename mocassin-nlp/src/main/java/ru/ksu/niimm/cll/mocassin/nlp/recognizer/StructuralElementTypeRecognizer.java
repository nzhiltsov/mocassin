package ru.ksu.niimm.cll.mocassin.nlp.recognizer;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

public interface StructuralElementTypeRecognizer {
	/**
	 * return prediction model for structural element types for given reference
	 * 
	 * @param reference
	 * @return
	 */
	@Deprecated
	StructuralElementTypesInfo recognize(Reference reference);

	/**
	 * predicts which type given element has
	 * 
	 * @param structuralElement
	 * @return
	 */
	MocassinOntologyClasses predict(StructuralElement structuralElement);
}
