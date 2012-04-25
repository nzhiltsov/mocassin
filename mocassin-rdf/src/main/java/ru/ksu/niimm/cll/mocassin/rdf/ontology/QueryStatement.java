package ru.ksu.niimm.cll.mocassin.rdf.ontology;

import java.util.List;

public class QueryStatement {
	private List<OntologyTriple> retrievedTriples;
	private boolean isInferenceOn;

	public QueryStatement(List<OntologyTriple> retrievedTriples) {
		this.retrievedTriples = retrievedTriples;
	}

	public List<OntologyTriple> getRetrievedTriples() {
		return retrievedTriples;
	}

	public void setRetrievedTriples(List<OntologyTriple> retrievedTriples) {
		this.retrievedTriples = retrievedTriples;
	}

	public boolean isInferenceOn() {
		return isInferenceOn;
	}

	public void setInferenceOn(boolean isInferenceOn) {
		this.isInferenceOn = isInferenceOn;
	}

}
