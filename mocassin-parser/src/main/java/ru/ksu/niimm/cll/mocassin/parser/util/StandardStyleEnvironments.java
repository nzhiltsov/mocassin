package ru.ksu.niimm.cll.mocassin.parser.util;

public enum StandardStyleEnvironments {
	ITEMIZE("itemize"), ENUMERATE("enumerate");

	private final String name;

	private StandardStyleEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new IllegalArgumentException(
					"a standard style name cannot be null");
		for (StandardStyleEnvironments e : StandardStyleEnvironments.values()) {
			String canonicalName = name.toLowerCase();
			if (e.name.equals(canonicalName)) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

}