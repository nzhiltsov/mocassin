package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Link.RelationTypes;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.PVNodeAdapter;

public class GraphStub {
	public static class NovelCharacterNodeAdapter implements
			PVNodeAdapter<DocumentSegment> {

		public String getNodeName(DocumentSegment t) {
			return t.getName();
		}

		public Object getNodeValue(DocumentSegment t) {
			return null;
		}
	}

	public enum SegmentTypes {

		THEOREM(1), SECTION(2), EQUATION(3), SUBSECTION(4), LEMMA(5), PROOF(6), PROPOSITION(7), COROLLARY(8);

		private int code;

		private SegmentTypes(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}
	}

	public static class DocumentSegment {

		private String name;

		private SegmentTypes type;

		private int numPage;

		public DocumentSegment(String name, SegmentTypes type, int numPage) {
			this.name = name;
			this.type = type;
			this.numPage = numPage;
		}

		public String getName() {
			return name;
		}

		public SegmentTypes getType() {
			return type;
		}

		public int getNumPage() {
			return numPage;
		}

	}

	public final static DocumentSegment[] CHARACTERS = new DocumentSegment[] {
			new DocumentSegment("The proof proper of Theorem 1.1",
					SegmentTypes.SUBSECTION, 4),// 0
			new DocumentSegment("Theorem 1.1", SegmentTypes.THEOREM, 2),// 1
			new DocumentSegment("Equation 2.7", SegmentTypes.EQUATION, 4),// 2
			new DocumentSegment("Lemma 2.1", SegmentTypes.LEMMA, 3),// 3
			new DocumentSegment("Lemma 2.2", SegmentTypes.LEMMA, 3),// 4
			new DocumentSegment("Equation 2.8", SegmentTypes.EQUATION, 5),// 5
			new DocumentSegment(
					"A corollary and a constructive approximating sequence",
					SegmentTypes.SUBSECTION, 6),// 6
			new DocumentSegment("Equation 1.2", SegmentTypes.EQUATION, 2),// 7
			new DocumentSegment("Proof of Proposition 3.1", SegmentTypes.PROOF,
					7), // 8
			new DocumentSegment("Equation 2.6", SegmentTypes.EQUATION, 4), // 9
			new DocumentSegment("Proof of Lemma 2.2", SegmentTypes.PROOF, 3), // 10
			new DocumentSegment("Corollary 3.1", SegmentTypes.COROLLARY, 6), // 11
			new DocumentSegment("Proposition 3.1", SegmentTypes.PROPOSITION, 6) // 12
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
