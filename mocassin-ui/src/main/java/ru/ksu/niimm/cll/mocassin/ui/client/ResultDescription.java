package ru.ksu.niimm.cll.mocassin.ui.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResultDescription implements Serializable {
	private String title;
	private String author;
	private String latexUri;
	private String pdfUri;
	private String relevantContextString;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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
