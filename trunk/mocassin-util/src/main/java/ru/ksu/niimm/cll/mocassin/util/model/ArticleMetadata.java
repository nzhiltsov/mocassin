package ru.ksu.niimm.cll.mocassin.util.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * arXiv article metadata
 * 
 * @author nzhiltsov
 * 
 */
public class ArticleMetadata {
	/**
	 * arXiv id, e.g. 'math/0205003' or 'ivm537'
	 */
	private String collectionId;
	/**
	 * full id, e.g. 'http://arxiv.org/abs/math/0205003v1' where the 'v1' suffix
	 * is a pointer of the version
	 */
	private String id;

	private List<Link> links = new ArrayList<Link>();

	private String title;

	private final Set<Author> authors = new HashSet<Author>();

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

	public void setAuthors(Set<Author> authors) {
		this.authors.addAll(authors);
	}

	public Set<Author> getAuthors() {
		return authors;
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

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

}
