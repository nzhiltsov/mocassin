package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import java.io.Serializable;

import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.protovis.LinkAdapter;

@SuppressWarnings("serial")
public class Graph implements Serializable {
	private Node[] nodes;
	private LinkAdapter[] links;

	public Graph() {
	}

	public Graph(Node[] nodes, LinkAdapter[] links) {
		this.nodes = nodes;
		this.links = links;
	}

	public Node[] getNodes() {
		return nodes;
	}

	public LinkAdapter[] getLinks() {
		return links;
	}

}
