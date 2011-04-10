package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Graph implements Serializable {
	private Node[] nodes;
	private Link[] links;
	public Graph() {}

	public Graph(Node[] nodes, Link[] links) {
		this.nodes = nodes;
		this.links = links;
	}

	public Node[] getNodes() {
		return nodes;
	}

	public Link[] getLinks() {
		return links;
	}
	

}
