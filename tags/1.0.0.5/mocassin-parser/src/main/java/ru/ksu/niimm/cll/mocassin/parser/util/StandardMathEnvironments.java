package ru.ksu.niimm.cll.mocassin.parser.util;

public enum StandardMathEnvironments {
	ARRAY("array"), EQUATION_ARRAY("eqnarray"), EQUATION("equation");
	private String name;

	private StandardMathEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new IllegalArgumentException(
					"a standard math name cannot be null");
		for (StandardMathEnvironments e : StandardMathEnvironments.values()) {
			if (e.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
}
