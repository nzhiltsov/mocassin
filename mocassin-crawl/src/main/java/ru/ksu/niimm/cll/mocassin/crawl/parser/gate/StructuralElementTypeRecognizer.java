package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;

public interface StructuralElementTypeRecognizer {
	

	/**
	 * predicts which type given element has
	 * 
	 * @param structuralElement
	 * @return
	 */
	MocassinOntologyClasses predict(StructuralElement structuralElement);

}
