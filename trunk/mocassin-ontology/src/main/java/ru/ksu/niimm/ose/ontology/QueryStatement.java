package ru.ksu.niimm.ose.ontology;

import java.util.List;

public class QueryStatement {
	private List<OntologyTriple> retrievedTriples;

	public QueryStatement(List<OntologyTriple> retrievedTriples) {
		this.retrievedTriples = retrievedTriples;
	}

	public List<OntologyTriple> getRetrievedTriples() {
		return retrievedTriples;
	}

	public void setRetrievedTriples(List<OntologyTriple> retrievedTriples) {
		this.retrievedTriples = retrievedTriples;
	}

}
