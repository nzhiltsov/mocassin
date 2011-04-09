package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.PVNodeAdapter;

public class Node {

	private String name;

	private NodeTypes type;

	private int numPage;

	public Node(String name, NodeTypes type, int numPage) {
		this.name = name;
		this.type = type;
		this.numPage = numPage;
	}

	public String getName() {
		return name;
	}

	public NodeTypes getType() {
		return type;
	}

	public int getNumPage() {
		return numPage;
	}

	public static class NovelCharacterNodeAdapter implements
			PVNodeAdapter<Node> {

		public String getNodeName(Node t) {
			return t.getName();
		}

		public Object getNodeValue(Node t) {
			return null;
		}
	}

	public enum NodeTypes {

		THEOREM(1), SECTION(2), EQUATION(3), SUBSECTION(4), LEMMA(5), PROOF(6), PROPOSITION(
				7), COROLLARY(8);

		private int code;

		private NodeTypes(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}
	}
}
