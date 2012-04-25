package ru.ksu.niimm.cll.mocassin.frontend.client;

@SuppressWarnings("serial")
public class OntLiteral extends OntElement {

	public OntLiteral() {
		super();
	}

	public OntLiteral(String label) {
		super("http://www.w3.org/2000/01/rdf-schema#Literal", label);
	}

}
