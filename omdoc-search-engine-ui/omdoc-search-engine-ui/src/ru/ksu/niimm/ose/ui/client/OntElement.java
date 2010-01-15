package ru.ksu.niimm.ose.ui.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OntElement implements Serializable, Comparable<OntElement> {
	protected String uri;
	protected String label;

	public OntElement() {
	}

	public OntElement(String uri, String label) {
		this.uri = uri;
		this.label = label;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "OntElement [label=" + label + ", uri=" + uri + "]";
	}

	@Override
	public int compareTo(OntElement o) {
		if (getLabel() != null && o.getLabel() != null) {
			getLabel().compareTo(o.getLabel());
		}
		return 0;
	}

}
