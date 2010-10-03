package ru.ksu.niimm.cll.mocassin.analyzer.similarity;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public interface StructuralElementTypeRecognizer {
	/**
	 * return prediction model for structural element types for given reference
	 * 
	 * @param reference
	 * @return
	 */
	StructuralElementTypesInfo recognize(Reference reference);
}
