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
package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

public enum SegmentTypes {
	AXIOM(0, "axiom"), CLAIM(1, "claim"), CONJECTURE(2, "conjecture"), COROLLARY(
			3, "corollary"), DEFINITION(4, "definition"), EQUATION(5,
			"equation"), EXAMPLE(6, "example"), FIGURE(7, "figure"), LEMMA(8,
			"lemma"), PROOF(9, "proof"), PROPOSITION(10, "proposition"), REMARK(
			11, "remark"), SECTION(12, "section"), THEOREM(13, "theorem"), UNRECOGNIZED_DOCUMENT_SEGMENT(
			14, "unrecognized segment");
	private final int code;
	private final String label;

	private SegmentTypes(int code, String label) {
		this.code = code;
		this.label = label;
	}

	public int getCode() {
		return code;
	}

	public static String getLabel(int code) {
		for (SegmentTypes type : SegmentTypes.values()) {
			if (type.code == code)
				return type.label;
		}
		throw new IllegalArgumentException("given a wrong type code: " + code);
	}

	public String getLabel() {
		return label;
	}

}
