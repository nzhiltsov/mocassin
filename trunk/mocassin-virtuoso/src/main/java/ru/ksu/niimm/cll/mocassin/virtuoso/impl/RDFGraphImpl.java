package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;

public class RDFGraphImpl implements RDFGraph {
	private String uri;

	public RDFGraphImpl(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
