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

enum Relations {// see MocassinOntologyRelations.java for predicate codes
	hasPart(0), refersTo(1), dependsOn(8), proves(9), hasConsequence(2), exemplifies(
			3), followedBy(10);

	protected final int code;

	private Relations(int code) {
		this.code = code;
	}

	static Relations fromCode(int code) {
		for (Relations rel : Relations.values()) {
			if (rel.code == code)
				return rel;
		}
		return null;
	}
}
