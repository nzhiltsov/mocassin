package ru.ksu.niimm.cll.mocassin.nlp.impl;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;

public class ParsedDocumentImpl implements ParsedDocument {
	private String filename;
	private long size;

	public ParsedDocumentImpl(String filename, long size) {
		this.filename = filename;
		this.size = size;
	}

	public String getFilename() {
		return filename;
	}

	public long getSize() {
		return size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
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
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}

}
