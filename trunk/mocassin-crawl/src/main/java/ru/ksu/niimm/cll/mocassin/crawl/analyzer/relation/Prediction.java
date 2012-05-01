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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;


public class Prediction {
	private final MocassinOntologyRelations relation;
	private final double confidence;

	public Prediction(MocassinOntologyRelations relation, double confidence) {
		this.relation = relation;
		this.confidence = confidence;
	}

	public MocassinOntologyRelations getRelation() {
		return relation;
	}

	public double getConfidence() {
		return confidence;
	}

	@Override
	public String toString() {
		return "Prediction [relation=" + relation + ", confidence="
				+ confidence + "]";
	}

	
}
