package ru.ksu.niimm.cll.mocassin.crawl.parser.util;

public enum StandardMathEnvironments {
	ARRAY("array"), EQUATION_ARRAY("eqnarray"), EQUATION("equation");
	private String name;

	private StandardMathEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new NullPointerException(
					"a standard math name cannot be null");
		for (StandardMathEnvironments e : StandardMathEnvironments.values()) {
			if (e.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
}
