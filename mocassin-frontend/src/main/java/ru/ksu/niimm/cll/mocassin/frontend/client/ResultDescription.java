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
package ru.ksu.niimm.cll.mocassin.frontend.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ResultDescription implements Serializable {
	private String title;
	private List<String> authors;
	private String documentUri;
	private String viewerUri;
	private String pdfUri;
	private String relevantContextString;

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

	public String getDocumentUri() {
		return documentUri;
	}

	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}

	public String getViewerUri() {
		return viewerUri;
	}

	public void setViewerUri(String viewerUri) {
		this.viewerUri = viewerUri;
	}

	public String getPdfUri() {
		return pdfUri;
	}

	public void setPdfUri(String pdfUri) {
		this.pdfUri = pdfUri;
	}

	public String getRelevantContextString() {
		return relevantContextString;
	}

	public void setRelevantContextString(String relevantContextString) {
		this.relevantContextString = relevantContextString;
	}

}
