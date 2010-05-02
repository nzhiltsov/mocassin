package ru.ksu.niimm.ose.ontology;

public class ArticleMetadata {
	/**
	 * f.e. 'http://example.org/document.omdoc'
	 */
	private String uri;
	private String title;
	private String author;

	public ArticleMetadata(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
