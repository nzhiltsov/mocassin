package ru.ksu.niimm.ose.ontology;

import java.util.List;

public class ArticleMetadata {
	/**
	 * f.e. 'http://example.org/document.omdoc'
	 */
	private String uri;
	private String title;
	private List<String> authors;

	public ArticleMetadata(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
