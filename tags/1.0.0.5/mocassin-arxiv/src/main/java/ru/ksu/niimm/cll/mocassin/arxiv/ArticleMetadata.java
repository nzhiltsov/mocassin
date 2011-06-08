package ru.ksu.niimm.cll.mocassin.arxiv;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;

/**
 * arXiv article metadata
 * 
 * @author nzhiltsov
 * 
 */
public class ArticleMetadata {
	/**
	 * arXiv id, e.g. 'math/0205003'
	 */
	private String arxivId;
	/**
	 * full id, e.g. 'http://arxiv.org/abs/math/0205003v1' where the 'v1' suffix
	 * is a pointer of the version
	 */
	private String id;

	private List<Link> links;

	private String title;

	private List<Author> authors;

	private String currentSegmentUri;

	private String currentSegmentTitle;

	private int currentPageNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getCurrentSegmentUri() {
		return currentSegmentUri;
	}

	public void setCurrentSegmentUri(String currentSegmentUri) {
		this.currentSegmentUri = currentSegmentUri;
	}

	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public String getCurrentSegmentTitle() {
		return currentSegmentTitle;
	}

	public void setCurrentSegmentTitle(String currentSegmentTitle) {
		this.currentSegmentTitle = currentSegmentTitle;
	}

	public String getArxivId() {
		return arxivId;
	}

	public void setArxivId(String arxivId) {
		this.arxivId = arxivId;
	}

}
