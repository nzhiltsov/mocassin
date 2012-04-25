package ru.ksu.niimm.cll.mocassin.crawl.parser.impl;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Edge;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.EdgeContext;

public class EdgeImpl implements Edge {

	private EdgeContext context;

	public void setContext(EdgeContext context) {
		this.context = context;
	}

	public EdgeContext getContext() {
		return context;
	}

}
