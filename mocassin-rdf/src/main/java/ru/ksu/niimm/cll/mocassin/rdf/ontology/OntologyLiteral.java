package ru.ksu.niimm.cll.mocassin.rdf.ontology;

public class OntologyLiteral extends OntologyElement {

	private static final String RDFS_LITERAL_URI = "http://www.w3.org/2000/01/rdf-schema#Literal";

	public OntologyLiteral(String label) {
		super(RDFS_LITERAL_URI, label);
	}

}
