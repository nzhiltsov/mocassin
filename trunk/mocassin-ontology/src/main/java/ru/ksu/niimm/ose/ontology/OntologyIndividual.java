package ru.ksu.niimm.ose.ontology;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

public class OntologyIndividual extends OntologyElement {
	private MocassinOntologyClasses type;

	public OntologyIndividual(String uri, String label) {
		super(uri, label);
	}

	public MocassinOntologyClasses getType() {
		return type;
	}

	public void setType(MocassinOntologyClasses type) {
		this.type = type;
	}

}
