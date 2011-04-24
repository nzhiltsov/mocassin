package ru.ksu.niimm.cll.mocassin.parser.util;

public enum StandardStyleEnvironments {
	ITEMIZE("itemize"), ENUMERATE("enumerate"), BIBLIOGRAPHY("thebibliography");

	private String name;

	private StandardStyleEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new IllegalArgumentException(
					"a standard style name cannot be null");
		for (StandardStyleEnvironments e : StandardStyleEnvironments.values()) {
			if (e.name.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
