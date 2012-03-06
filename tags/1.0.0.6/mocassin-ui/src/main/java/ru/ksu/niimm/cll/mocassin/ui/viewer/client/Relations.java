package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

enum Relations {// see MocassinOntologyRelations.java for predicate codes
	hasPart(0), refersTo(1), dependsOn(8), proves(9), hasConsequence(2), exemplifies(
			3);

	protected final int code;

	private Relations(int code) {
		this.code = code;
	}

	static Relations fromCode(int code) {
		for (Relations rel : Relations.values()) {
			if (rel.code == code)
				return rel;
		}
		return null;
	}
}