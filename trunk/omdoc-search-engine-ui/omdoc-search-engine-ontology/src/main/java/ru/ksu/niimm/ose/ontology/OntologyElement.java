package ru.ksu.niimm.ose.ontology;

public class OntologyElement {

	protected String label;
	protected String uri;

	public OntologyElement(String uri, String label) {
		this.label = label;
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}