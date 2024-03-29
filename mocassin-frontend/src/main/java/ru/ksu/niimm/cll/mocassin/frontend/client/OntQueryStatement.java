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
package ru.ksu.niimm.cll.mocassin.frontend.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class OntQueryStatement implements Serializable {
	private OntElement retrievedConcept;
	private List<OntTriple> triples;
	private boolean isInferenceOn;

	public OntQueryStatement() {
	}

	public OntQueryStatement(OntElement retrievedConcept,
			List<OntTriple> triples, boolean isInferenceOn) {
		this.retrievedConcept = retrievedConcept;
		this.triples = triples;
		this.isInferenceOn = isInferenceOn;
	}

	public OntElement getRetrievedConcept() {
		return retrievedConcept;
	}

	public void setRetrievedConcept(OntElement retrievedConcept) {
		this.retrievedConcept = retrievedConcept;
	}

	public List<OntTriple> getTriples() {
		return triples;
	}

	public void setTriples(List<OntTriple> triples) {
		this.triples = triples;
	}

	public boolean isInferenceOn() {
		return isInferenceOn;
	}

	public void setInferenceOn(boolean isInferenceOn) {
		this.isInferenceOn = isInferenceOn;
	}

}
