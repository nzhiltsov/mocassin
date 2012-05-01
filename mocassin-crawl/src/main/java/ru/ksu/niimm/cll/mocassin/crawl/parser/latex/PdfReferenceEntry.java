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
