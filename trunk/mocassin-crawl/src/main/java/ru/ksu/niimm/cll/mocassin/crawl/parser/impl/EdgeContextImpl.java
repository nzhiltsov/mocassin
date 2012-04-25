package ru.ksu.niimm.cll.mocassin.crawl.parser.impl;

import javax.xml.bind.annotation.XmlType;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.EdgeContext;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.EdgeType;

@XmlType
public class EdgeContextImpl implements EdgeContext {
	private EdgeType type;
	private String aroundText;
	private String refId;

	/**
	 * no-args constructor for JAXB
	 */
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

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	@Override
	public String toString() {
		return getEdgeType().toString();
	}

}
