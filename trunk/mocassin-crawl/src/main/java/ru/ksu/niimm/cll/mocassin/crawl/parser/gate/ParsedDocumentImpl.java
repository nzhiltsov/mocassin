/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;

public class ParsedDocumentImpl implements ParsedDocument {
	private final String arxivId;
	private final String uri;
	private long size;
	private final String pdfUri;

	public ParsedDocumentImpl(String arxivId, String uri, String pdfUri) {
		this.arxivId = arxivId;
		this.uri = uri;
		this.pdfUri = pdfUri;
	}
	public ParsedDocumentImpl(String arxivId, String uri, String pdfUri, long size) {
		this.arxivId = arxivId;
		this.uri = uri;
		this.pdfUri = pdfUri;
		this.size = size;
	}


	public String getUri() {
		return uri;
	}

	public long getSize() {
		return size;
	}

	public String getPdfUri() {
		return pdfUri;
	}

	public String getCollectionId() {
		return arxivId;
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
