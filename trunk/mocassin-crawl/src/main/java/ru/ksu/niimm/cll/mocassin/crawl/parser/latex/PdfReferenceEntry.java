package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import net.sourceforge.texlipse.model.ReferenceEntry;

public class PdfReferenceEntry {
	private final ReferenceEntry reference;
	private int pdfNumberPage;

	public PdfReferenceEntry(ReferenceEntry reference, int pdfNumberPage) {
		this.reference = reference;
		this.pdfNumberPage = pdfNumberPage;
	}

	public ReferenceEntry getReference() {
		return reference;
	}

	public int getPdfNumberPage() {
		return pdfNumberPage;
	}

	public String key() {
		return reference.key;
	}

	public int startLine() {
		return reference.startLine;
	}
}
