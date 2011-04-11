package ru.ksu.niimm.ose.ontology;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

public class ABoxTriple {
	private OntologyIndividual subject;
	private MocassinOntologyRelations predicate;
	private OntologyIndividual object;

	public ABoxTriple(OntologyIndividual subject,
			MocassinOntologyRelations predicate, OntologyIndividual object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public OntologyIndividual getSubject() {
		return subject;
	}

	public MocassinOntologyRelations getPredicate() {
		return predicate;
	}

	public OntologyIndividual getObject() {
		return object;
	}

}
