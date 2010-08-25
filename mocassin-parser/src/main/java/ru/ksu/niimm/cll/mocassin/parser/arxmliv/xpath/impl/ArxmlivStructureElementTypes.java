package ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl;

public enum ArxmlivStructureElementTypes {
	SECTION("section"), SUBSECTION("subsection"), THEOREM("theorem"), EQUATION(
			"equation");

	private String label;

	private ArxmlivStructureElementTypes(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

}
