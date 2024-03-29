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
package ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv;

import java.util.HashSet;
import java.util.Set;

public enum ArxmlivStructureElementTypes {
	SECTION("section"), SUBSECTION("subsection"), THEOREM("theorem"), PROOF(
			"proof"), EQUATION("equation"), EQUATION_GROUP("equationgroup"), FIGURE(
			"figure"), DOCUMENT("document"), TABLE("tabular");

	private String label;

	private ArxmlivStructureElementTypes(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public static boolean hasName(String name) {
		for (ArxmlivStructureElementTypes type : values()) {
			if (type.toString().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static Set<String> toNameSet() {
		Set<String> nameSet = new HashSet<String>();
		for (ArxmlivStructureElementTypes type : values()) {
			nameSet.add(type.toString());
		}
		return nameSet;
	}
}
