package ru.ksu.niimm.cll.mocassin.parser.impl;

import ru.ksu.niimm.cll.mocassin.parser.Node;

public class NodeImpl implements Node {
	private String id;
	private String name;

	public NodeImpl(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return String.format("[%s/ %s]", getId(), getName());
	}

}
