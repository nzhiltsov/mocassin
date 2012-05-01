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

public class Triple<S, P, O> {
	private final S s;
	private final P p;
	private final O o;

	public Triple(S s, P p, O o) {
		this.s = s;
		this.p = p;
		this.o = o;
	}

	public S getS() {
		return s;
	}

	public P getP() {
		return p;
	}

	public O getO() {
		return o;
	}

}
