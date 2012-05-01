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
package ru.ksu.niimm.cll.mocassin.rdf.ontology;

import java.util.List;

public class QueryStatement {
	private List<OntologyTriple> retrievedTriples;
	private boolean isInferenceOn;

	public QueryStatement(List<OntologyTriple> retrievedTriples) {
		this.retrievedTriples = retrievedTriples;
	}

	public List<OntologyTriple> getRetrievedTriples() {
		return retrievedTriples;
	}

	public void setRetrievedTriples(List<OntologyTriple> retrievedTriples) {
		this.retrievedTriples = retrievedTriples;
	}

	public boolean isInferenceOn() {
		return isInferenceOn;
	}

	public void setInferenceOn(boolean isInferenceOn) {
		this.isInferenceOn = isInferenceOn;
	}

}
