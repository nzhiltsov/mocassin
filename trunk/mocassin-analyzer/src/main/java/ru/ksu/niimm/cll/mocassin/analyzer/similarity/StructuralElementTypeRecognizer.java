package ru.ksu.niimm.cll.mocassin.analyzer.similarity;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;

public interface StructuralElementTypeRecognizer {
	/**
	 * return prediction model for structural element types for given reference
	 * 
	 * @param reference
	 * @return
	 */
	StructuralElementTypesInfo recognize(Reference reference);

	/**
	 * predicts which type given element has
	 * 
	 * @param structuralElement
	 * @return
	 */
	MocassinOntologyClasses predict(StructuralElement structuralElement);
}
