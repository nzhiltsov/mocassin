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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;


public class AbstractPartionableFeature {

	protected String[] left;

	protected String[] right;

	public AbstractPartionableFeature(String[] left, String[] right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < left.length; i++) {
			String value = left[i] != null ? left[i] : "";
			sb.append(value);
			if (i < left.length - 1) {
				sb.append("|");
			}

		}
		sb.append("|");
		for (int i = 0; i < right.length; i++) {
			String value = right[i] != null ? right[i] : "";
			sb.append(value);
			if (i < right.length - 1) {
				sb.append("|");
			}

		}
		return sb.toString();
	}

}
