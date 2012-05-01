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
package ru.ksu.niimm.cll.mocassin.util;

import java.util.List;

public class GateDocumentMetadata {
	private final String name;
	private final String title;
	private final List<String> authorNames;

	public GateDocumentMetadata(String name, String title,
			List<String> authorNames) {
		this.name = name;
		this.title = title;
		this.authorNames = authorNames;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getAuthorNames() {
		return authorNames;
	}

}
