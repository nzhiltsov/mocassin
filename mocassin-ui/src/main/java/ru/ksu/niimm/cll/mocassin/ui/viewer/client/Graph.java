package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

public class Graph {
	private Node[] nodes;
	private Link[] links;

	private Graph(Node[] nodes, Link[] links) {
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
