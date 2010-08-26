package ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl;

public enum ArxmlivStructureElementTypes {
	SECTION("section"), SUBSECTION("subsection"), THEOREM("theorem"), PROOF(
			"proof"), EQUATION("equation");

	private String label;

	private ArxmlivStructureElementTypes(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public static boolean hasName(String name) {
		for (ArxmlivStructureElementTypes type : values()) {
			if (type.toString().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
