package ru.ksu.niimm.cll.mocassin.ui.client;

import java.io.Serializable;
import java.util.List;
@SuppressWarnings("serial")
public class OntQueryStatement implements Serializable{
	private OntElement retrievedConcept;
	private List<OntTriple> triples;

	public OntQueryStatement() {
	}

	public OntQueryStatement(OntElement retrievedConcept,
			List<OntTriple> triples) {
		this.retrievedConcept = retrievedConcept;
		this.triples = triples;
	}

	public OntElement getRetrievedConcept() {
		return retrievedConcept;
	}

	public void setRetrievedConcept(OntElement retrievedConcept) {
		this.retrievedConcept = retrievedConcept;
	}

	public List<OntTriple> getTriples() {
		return triples;
	}

	public void setTriples(List<OntTriple> triples) {
		this.triples = triples;
	}

}
