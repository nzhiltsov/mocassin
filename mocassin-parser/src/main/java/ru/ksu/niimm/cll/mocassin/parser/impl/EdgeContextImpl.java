package ru.ksu.niimm.cll.mocassin.parser.impl;

import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;

public class EdgeContextImpl implements EdgeContext {
	private EdgeType type;
	private String aroundText;

	public EdgeContextImpl(EdgeType type) {
		this.type = type;
	}

	@Override
	public EdgeType getEdgeType() {
		return this.type;
	}

	public String getAroundText() {
		return aroundText;
	}

	public void setAroundText(String aroundText) {
		this.aroundText = aroundText;
	}

	@Override
	public String toString() {
		return String.format("%s", getAroundText());
	}

}
