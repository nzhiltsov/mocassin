package ru.ksu.niimm.cll.mocassin.parser.impl;

import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;

public class EdgeContextImpl implements EdgeContext {
	private EdgeType type;

	public EdgeContextImpl(EdgeType type) {
		this.type = type;
	}

	@Override
	public EdgeType getEdgeType() {
		return this.type;
	}

	@Override
	public String toString() {
		return getEdgeType().toString();
	}

}
