package ru.ksu.niimm.cll.mocassin.ontology;


public class SGEdge {

	private final int fromNumPage;
	private final int toNumPage;
	private final String fromTitle;
	private final String toTitle;
	private final ABoxTriple triple;

	public SGEdge(ABoxTriple triple, int fromNumPage, int toNumPage,
			String fromTitle, String toTitle) {
		this.triple = triple;
		this.fromNumPage = fromNumPage;
		this.toNumPage = toNumPage;
		this.fromTitle = fromTitle;
		this.toTitle = toTitle;
	}

	public int getFromNumPage() {
		return fromNumPage;
	}

	public int getToNumPage() {
		return toNumPage;
	}

	public ABoxTriple getTriple() {
		return triple;
	}

	public String getFromTitle() {
		return fromTitle;
	}

	public String getToTitle() {
		return toTitle;
	}

	public OntologyIndividual getSubject() {
		return triple.getSubject();
	}

	public MocassinOntologyRelations getPredicate() {
		return triple.getPredicate();
	}

	public OntologyIndividual getObject() {
		return triple.getObject();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((triple == null) ? 0 : triple.hashCode());
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
		SGEdge other = (SGEdge) obj;
		if (triple == null) {
			if (other.triple != null)
				return false;
		} else if (!triple.equals(other.triple))
			return false;
		return true;
	}

}
