package ru.ksu.niimm.cll.mocassin.util.model;

import com.google.common.base.Predicate;

public class Link {
	private static final String PDF_MIME_TYPE = "application/pdf";
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

	public static Link nullPdfLink() {
		Link link = new Link();
		link.setHref("");
		link.setType(PDF_MIME_TYPE);
		return link;
	}
	
	public static Link pdfLink(String href) {
		Link link = new Link();
		link.setHref(href);
		link.setType(PDF_MIME_TYPE);
		return link;
	}

	public static class PdfLinkPredicate implements Predicate<Link> {

		@Override
		public boolean apply(Link link) {
			return link.getType().equals(PDF_MIME_TYPE);
		}

	}
}
