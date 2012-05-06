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

import java.util.SortedMap;

public class StructuralElementTypesInfoImpl implements
		StructuralElementTypesInfo {
	private Reference reference;
	private SortedMap<String, Float> fromElementVector;
	private SortedMap<String, Float> toElementVector;

	public StructuralElementTypesInfoImpl(Reference reference,
			SortedMap<String, Float> fromElementVector,
			SortedMap<String, Float> toElementVector) {
		this.reference = reference;
		this.fromElementVector = fromElementVector;
		this.toElementVector = toElementVector;
	}

	public Reference getReference() {
		return reference;
	}

	public SortedMap<String, Float> getFromElementVector() {
		return fromElementVector;
	}

	public SortedMap<String, Float> getToElementVector() {
		return toElementVector;
	}

}
