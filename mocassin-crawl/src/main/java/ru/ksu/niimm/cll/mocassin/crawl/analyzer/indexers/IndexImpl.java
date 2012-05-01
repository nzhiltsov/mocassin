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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.indexers;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;


import com.aliasi.matrix.Vector;

public class IndexImpl implements Index {
	private Map<Reference, Vector> referenceVectors;
	private List<String> terms;

	public IndexImpl(Map<Reference, Vector> referenceVectors,
			List<String> terms) {
		this.referenceVectors = referenceVectors;
		this.terms = terms;
	}

	public Map<Reference, Vector> getReferenceVectors() {
		return referenceVectors;
	}

	public List<String> getTerms() {
		return terms;
	}

}
