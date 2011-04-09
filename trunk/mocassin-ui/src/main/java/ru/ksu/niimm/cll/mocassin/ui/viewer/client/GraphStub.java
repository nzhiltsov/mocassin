package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Node.NodeTypes;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Link.RelationTypes;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.PVNodeAdapter;

public class GraphStub {
	
	

	

	public final static Node[] CHARACTERS = new Node[] {
			new Node("The proof proper of Theorem 1.1",
					NodeTypes.SUBSECTION, 4),// 0
			new Node("Theorem 1.1", NodeTypes.THEOREM, 2),// 1
			new Node("Equation 2.7", NodeTypes.EQUATION, 4),// 2
			new Node("Lemma 2.1", NodeTypes.LEMMA, 3),// 3
			new Node("Lemma 2.2", NodeTypes.LEMMA, 3),// 4
			new Node("Equation 2.8", NodeTypes.EQUATION, 5),// 5
			new Node(
					"A corollary and a constructive approximating sequence",
					NodeTypes.SUBSECTION, 6),// 6
			new Node("Equation 1.2", NodeTypes.EQUATION, 2),// 7
			new Node("Proof of Proposition 3.1", NodeTypes.PROOF,
					7), // 8
			new Node("Equation 2.6", NodeTypes.EQUATION, 4), // 9
			new Node("Proof of Lemma 2.2", NodeTypes.PROOF, 3), // 10
			new Node("Corollary 3.1", NodeTypes.COROLLARY, 6), // 11
			new Node("Proposition 3.1", NodeTypes.PROPOSITION, 6) // 12
	};

	public static final Link[] LINKS = new Link[] {
			new Link(1, 0, RelationTypes.PROVES),
			new Link(1, 0, RelationTypes.REFERS_TO),
			new Link(2, 0, RelationTypes.DEPENDS_ON),
			new Link(3, 0, RelationTypes.DEPENDS_ON),
			new Link(4, 0, RelationTypes.DEPENDS_ON),
			new Link(5, 0, RelationTypes.DEPENDS_ON),
			new Link(5, 0, RelationTypes.DEPENDS_ON),
			new Link(3, 0, RelationTypes.DEPENDS_ON),
			new Link(6, 1, RelationTypes.REFERS_TO),
			new Link(6, 3, RelationTypes.REFERS_TO),
			new Link(6, 7, RelationTypes.REFERS_TO),
			new Link(8, 9, RelationTypes.DEPENDS_ON),
			new Link(8, 3, RelationTypes.DEPENDS_ON),
			new Link(10, 4, RelationTypes.PROVES),
			new Link(11, 1, RelationTypes.HAS_CONSEQUENCE),
			new Link(8, 12, RelationTypes.PROVES)};
}
