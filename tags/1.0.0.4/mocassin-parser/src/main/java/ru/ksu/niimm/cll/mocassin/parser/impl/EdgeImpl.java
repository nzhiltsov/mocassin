package ru.ksu.niimm.cll.mocassin.parser.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.Node;

@XmlType(name = "edge")
public class EdgeImpl implements Edge<Node, Node> {
	@XmlElement
	private Node from;
	@XmlElement
	private Node to;
	@XmlElement
	private EdgeContext context;

	@Override
	public void connect(Node from, Node to, EdgeContext context) {
		this.from = from;
		this.to = to;
		this.context = context;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	public EdgeContext getContext() {
		return context;
	}

	@Override
	public String toString() {
		return String.format("%s|%s|%s", getFrom(), getTo(), getContext());
	}

}
