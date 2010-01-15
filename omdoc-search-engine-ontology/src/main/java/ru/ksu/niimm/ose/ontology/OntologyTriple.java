package ru.ksu.niimm.ose.ontology;

public class OntologyTriple {
	private OntologyConcept subject;
	private OntologyRelation predicate;
	private OntologyElement object;

	public OntologyTriple(OntologyConcept subject, OntologyRelation predicate,
			OntologyElement object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public OntologyConcept getSubject() {
		return subject;
	}

	public void setSubject(OntologyConcept subject) {
		this.subject = subject;
	}

	public OntologyRelation getPredicate() {
		return predicate;
	}

	public void setPredicate(OntologyRelation predicate) {
		this.predicate = predicate;
	}

	public OntologyElement getObject() {
		return object;
	}

	public void setObject(OntologyElement object) {
		this.object = object;
	}

}
