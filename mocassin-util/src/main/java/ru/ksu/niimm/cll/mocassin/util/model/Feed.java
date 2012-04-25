package ru.ksu.niimm.cll.mocassin.util.model;


public class Feed {
	private Link link;
	private ArticleMetadata entry;

	public ArticleMetadata getEntry() {
		return entry;
	}

	public void setEntry(ArticleMetadata entry) {
		this.entry = entry;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

}
