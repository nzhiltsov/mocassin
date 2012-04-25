package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import java.io.Serializable;
import java.util.List;

public class ArticleInfo implements Serializable {
	private String uri;
	private String key;
	private String title;
	private List<String> authors;
	private String currentSegmentUri;
	private int currentPageNumber = 1;
	private String pdfUri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
		if (currentPageNumber > 0) {
			this.currentPageNumber = currentPageNumber;
		}
	}

	public String getPdfUri() {
		return pdfUri;
	}

	public void setPdfUri(String pdfUri) {
		this.pdfUri = pdfUri;
	}

}
