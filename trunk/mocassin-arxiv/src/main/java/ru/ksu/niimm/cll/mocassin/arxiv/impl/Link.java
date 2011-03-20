package ru.ksu.niimm.cll.mocassin.arxiv.impl;

public class Link {
	/**
	 * URL of a version, e.g. 'http://arxiv.org/pdf/math/0205003v1'
	 */
	private String href;
	/**
	 * type of a link, e.g. 'application/pdf'
	 */
	private String type;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
