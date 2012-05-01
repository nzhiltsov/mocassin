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

public enum StandardStyleEnvironments {
	ITEMIZE("itemize"), ENUMERATE("enumerate"), CENTERING("centering"), CENTER("center"), NORMALSIZE(
			"normalsize");

	private final String name;

	private StandardStyleEnvironments(String name) {
		this.name = name;
	}

	public static boolean contains(String name) {
		if (name == null)
			throw new NullPointerException(
					"a standard style name cannot be null");
		for (StandardStyleEnvironments e : StandardStyleEnvironments.values()) {
			String canonicalName = name.toLowerCase();
			if (e.name.equals(canonicalName)) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

}
