package ru.ksu.niimm.ose.ontology;

public class OntologyTriple {
	private OntologyConcept subject;
	private OntologyElement predicate;
	private OntologyElement object;

	public OntologyTriple(OntologyConcept subject, OntologyElement predicate,
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

	public OntologyElement getPredicate() {
		return predicate;
	}

	public void setPredicate(OntologyElement predicate) {
		this.predicate = predicate;
	}

	public OntologyElement getObject() {
		return object;
	}

	public void setObject(OntologyElement object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return String.format("<%s %s %s>", subject, predicate, object);
	}

}
