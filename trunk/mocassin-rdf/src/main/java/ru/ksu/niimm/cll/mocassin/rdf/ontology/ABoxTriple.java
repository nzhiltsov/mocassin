package ru.ksu.niimm.cll.mocassin.rdf.ontology;


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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ABoxTriple other = (ABoxTriple) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

}
