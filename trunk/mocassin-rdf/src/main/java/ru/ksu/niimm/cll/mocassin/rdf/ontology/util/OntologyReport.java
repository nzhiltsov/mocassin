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
package ru.ksu.niimm.cll.mocassin.rdf.ontology.util;

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
