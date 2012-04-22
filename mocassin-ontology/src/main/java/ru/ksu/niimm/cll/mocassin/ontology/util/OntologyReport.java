package ru.ksu.niimm.cll.mocassin.ontology.util;

import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class OntologyReport {
	private final XWPFDocument wordDocument;
	private final Set<String> classesWithEmptyComments;

	OntologyReport(XWPFDocument wordDocument,
			Set<String> classesWithEmptyComments) {
		this.wordDocument = wordDocument;
		this.classesWithEmptyComments = classesWithEmptyComments;
	}

	public XWPFDocument getWordDocument() {
		return wordDocument;
	}

	public Set<String> getClassesWithEmptyComments() {
		return classesWithEmptyComments;
	}

}
