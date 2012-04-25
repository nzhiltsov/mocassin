package ru.ksu.niimm.cll.mocassin.frontend.dashboard.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ArxivArticleMetadata implements Serializable {
	private String key;
	private String title;
	
	

	public ArxivArticleMetadata() {
	}

	public ArxivArticleMetadata(String key, String title) {
		this.key = key;
		this.title = title;
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

}
