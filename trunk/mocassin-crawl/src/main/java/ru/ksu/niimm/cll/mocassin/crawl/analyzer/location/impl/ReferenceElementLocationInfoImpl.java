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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.impl;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.ReferenceElementLocationInfo;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;

public class ReferenceElementLocationInfoImpl implements ReferenceElementLocationInfo {
	private Reference reference;

	private float startDistance;

	private float endDistance;

	public ReferenceElementLocationInfoImpl(Reference reference, float startDistance,
			float endDistance) {
		this.reference = reference;
		this.startDistance = startDistance;
		this.endDistance = endDistance;
	}

	public Reference getReference() {
		return reference;
	}

	public float getStartDistance() {
		return startDistance;
	}

	public float getEndDistance() {
		return endDistance;
	}

}
