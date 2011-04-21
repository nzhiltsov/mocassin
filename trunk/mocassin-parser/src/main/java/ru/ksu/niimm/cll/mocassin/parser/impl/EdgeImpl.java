package ru.ksu.niimm.cll.mocassin.parser.impl;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;

public class EdgeImpl implements Edge {

	private EdgeContext context;

	public void setContext(EdgeContext context) {
		this.context = context;
	}

	public EdgeContext getContext() {
		return context;
	}

}
