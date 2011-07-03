package ru.ksu.niimm.cll.mocassin.util;

import java.util.List;

public class GateDocumentMetadata {
	private final String name;
	private final String title;
	private final List<String> authorNames;

	public GateDocumentMetadata(String name, String title,
			List<String> authorNames) {
		this.name = name;
		this.title = title;
		this.authorNames = authorNames;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getAuthorNames() {
		return authorNames;
	}

}
