package unittest.evaluation;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

public class ContingencyTable {
	private static final int n = MocassinOntologyClasses.values().length + 1;
	protected int[][] matrix = new int[n][n];

	public ContingencyTable() {
		init();
	}

	public void init() {
		matrix[MocassinOntologyClasses.AXIOM.ordinal()][MocassinOntologyClasses.AXIOM
				.ordinal()] = 5;
		matrix[MocassinOntologyClasses.CLAIM.ordinal()][MocassinOntologyClasses.CLAIM
				.ordinal()] = 113;
		matrix[MocassinOntologyClasses.CONJECTURE.ordinal()][MocassinOntologyClasses.CONJECTURE
				.ordinal()] = 148;
		matrix[MocassinOntologyClasses.COROLLARY.ordinal()][MocassinOntologyClasses.COROLLARY
				.ordinal()] = 1670;
		matrix[MocassinOntologyClasses.DEFINITION.ordinal()][MocassinOntologyClasses.DEFINITION
				.ordinal()] = 1827;
		matrix[MocassinOntologyClasses.EQUATION.ordinal()][MocassinOntologyClasses.EQUATION
				.ordinal()] = 71314;
		matrix[MocassinOntologyClasses.EXAMPLE.ordinal()][MocassinOntologyClasses.EXAMPLE
				.ordinal()] = 755;
		matrix[MocassinOntologyClasses.LEMMA.ordinal()][MocassinOntologyClasses.LEMMA
				.ordinal()] = 3976;
		matrix[MocassinOntologyClasses.PROOF.ordinal()][MocassinOntologyClasses.PROOF
				.ordinal()] = 4910;
		matrix[MocassinOntologyClasses.PROPOSITION.ordinal()][MocassinOntologyClasses.PROPOSITION
				.ordinal()] = 3048;
		matrix[MocassinOntologyClasses.REMARK.ordinal()][MocassinOntologyClasses.REMARK
				.ordinal()] = 2067;
		matrix[MocassinOntologyClasses.SECTION.ordinal()][MocassinOntologyClasses.SECTION
				.ordinal()] = 8260;
		matrix[MocassinOntologyClasses.THEOREM.ordinal()][MocassinOntologyClasses.THEOREM
				.ordinal()] = 4588;
	}

	public void put(MocassinOntologyClasses prediction,
			MocassinOntologyClasses real) {
		int predIndex = prediction != MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT ? prediction.ordinal() : n - 1;
		int realIndex = real != MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT ? real.ordinal() : n - 1;
		matrix[predIndex][realIndex] = matrix[predIndex][realIndex] + 1;
	}

}
