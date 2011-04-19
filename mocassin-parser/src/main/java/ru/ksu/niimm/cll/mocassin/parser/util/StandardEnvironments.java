package ru.ksu.niimm.cll.mocassin.parser.util;

public enum StandardEnvironments {
	ITEMIZE("itemize"), ENUMERATE("enumerate");

	private String name;

	private StandardEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new IllegalArgumentException("a standard name cannot be null");
		for (StandardEnvironments e : StandardEnvironments.values()) {
			if (e.name.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
