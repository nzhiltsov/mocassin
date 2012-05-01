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
package ru.ksu.niimm.cll.mocassin.crawl.parser.util;

public enum StandardMathEnvironments {
	ARRAY("array"), EQUATION_ARRAY("eqnarray"), EQUATION("equation");
	private String name;

	private StandardMathEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new NullPointerException(
					"a standard math name cannot be null");
		for (StandardMathEnvironments e : StandardMathEnvironments.values()) {
			if (e.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
}
