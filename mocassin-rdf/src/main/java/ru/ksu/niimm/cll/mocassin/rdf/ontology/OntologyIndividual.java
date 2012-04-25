package ru.ksu.niimm.cll.mocassin.rdf.ontology;


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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		OntologyIndividual other = (OntologyIndividual) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
