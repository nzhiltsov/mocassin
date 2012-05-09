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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.info;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;


public class PredictedPairInfo {
	private MocassinOntologyClasses fromType;
	private MocassinOntologyClasses toType;
	private Reference reference;

	public PredictedPairInfo(MocassinOntologyClasses fromType,
			MocassinOntologyClasses toType) {

		this.fromType = fromType;
		this.toType = toType;
	}

	public MocassinOntologyClasses getFromType() {
		return fromType;
	}

	public MocassinOntologyClasses getToType() {
		return toType;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

}
