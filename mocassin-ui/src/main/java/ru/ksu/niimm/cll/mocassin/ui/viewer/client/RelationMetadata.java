package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

public class RelationMetadata {
	private final Relations type;
	private final Node from;
	private final Node to;

	public RelationMetadata(Node from, Node to, Relations type) {
		this.from = from;
		this.to = to;
		this.type = type;
	}

	public Relations getType() {
		return type;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

}
