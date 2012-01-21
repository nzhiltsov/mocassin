package ru.ksu.niimm.cll.mocassin.parser.util;

public enum StandardMetadataEnvironments {
	BIBLIOGRAPHY("thebibliography");
	private final String name;

	private StandardMetadataEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new IllegalArgumentException(
					"a standard metadata name cannot be null");
		for (StandardMetadataEnvironments e : StandardMetadataEnvironments
				.values()) {
			if (e.name.equals(name)) {
				return true;
			}
		}
		return false;
	}

}