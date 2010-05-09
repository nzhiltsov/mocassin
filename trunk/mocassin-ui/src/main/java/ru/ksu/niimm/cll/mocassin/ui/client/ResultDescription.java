package ru.ksu.niimm.cll.mocassin.ui.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ResultDescription implements Serializable {
	private String title;
	private List<String> authors;
	private String documentUri;
	private String latexUri;
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

	public String getLatexUri() {
		return latexUri;
	}

	public void setLatexUri(String latexUri) {
		this.latexUri = latexUri;
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
