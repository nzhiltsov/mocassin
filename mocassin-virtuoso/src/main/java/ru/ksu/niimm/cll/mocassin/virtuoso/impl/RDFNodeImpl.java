package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFNode;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFNode.Type;

public class RDFNodeImpl implements RDFNode {
	private String uri;
	private Type type;

	public RDFNodeImpl(String uri, Type type) {
		this.uri = uri;
		this.type = type;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
