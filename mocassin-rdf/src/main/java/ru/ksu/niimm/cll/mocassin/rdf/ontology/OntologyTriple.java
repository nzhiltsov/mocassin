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

public class OntologyTriple {
	private OntologyElement subject;
	private OntologyElement predicate;
	private OntologyElement object;

	public OntologyTriple(OntologyElement subject, OntologyElement predicate,
			OntologyElement object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public OntologyElement getSubject() {
		return subject;
	}

	public void setSubject(OntologyConcept subject) {
		this.subject = subject;
	}

	public OntologyElement getPredicate() {
		return predicate;
	}

	public void setPredicate(OntologyElement predicate) {
		this.predicate = predicate;
	}

	public OntologyElement getObject() {
		return object;
	}

	public void setObject(OntologyElement object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return String.format("<%s %s %s>", subject, predicate, object);
	}

}
