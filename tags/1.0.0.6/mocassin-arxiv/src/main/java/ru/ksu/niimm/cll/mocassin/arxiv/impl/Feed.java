package ru.ksu.niimm.cll.mocassin.arxiv.impl;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;

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
