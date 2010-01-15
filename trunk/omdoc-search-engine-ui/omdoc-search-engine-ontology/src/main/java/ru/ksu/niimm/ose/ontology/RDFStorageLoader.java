package ru.ksu.niimm.ose.ontology;

import com.hp.hpl.jena.ontology.OntModel;

public interface RDFStorageLoader {
	/**
	 * receive model of RDF storage
	 * 
	 * @return
	 */
	OntModel getRdfStorage();

}