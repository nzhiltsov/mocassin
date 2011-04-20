package ru.ksu.niimm.cll.mocassin.nlp.impl;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;

public class ParsedDocumentImpl implements ParsedDocument {
	private String uri;
	private long size;
	private String pdfUri;

	public ParsedDocumentImpl(String uri) {
		this.uri = uri;
	}

	public ParsedDocumentImpl(String filename, String pdfUri) {
		this.uri = filename;
		this.pdfUri = pdfUri;
	}

	public ParsedDocumentImpl(String filename, long size) {
		this.uri = filename;
		this.size = size;
	}

	public String getFilename() {
		return uri;
	}

	public long getSize() {
		return size;
	}

	public String getPdfUri() {
		return pdfUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParsedDocumentImpl other = (ParsedDocumentImpl) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
