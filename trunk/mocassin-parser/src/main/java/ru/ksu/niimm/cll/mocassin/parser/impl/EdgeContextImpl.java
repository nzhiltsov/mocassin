package ru.ksu.niimm.cll.mocassin.parser.impl;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;

@XmlType
public class EdgeContextImpl implements EdgeContext {
	private EdgeType type;
	private String aroundText;

	private EdgeContextImpl() {
	}

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
