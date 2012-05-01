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
@SuppressWarnings("serial")
public class OntTriple implements Serializable{
	private OntElement subject;
	private OntElement predicate;
	private OntElement object;

	public OntTriple() {
	}

	public OntTriple(OntElement subject, OntElement predicate, OntElement object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public OntElement getSubject() {
		return subject;
	}

	public void setSubject(OntElement subject) {
		this.subject = subject;
	}

	public OntElement getPredicate() {
		return predicate;
	}

	public void setPredicate(OntElement predicate) {
		this.predicate = predicate;
	}

	public OntElement getObject() {
		return object;
	}

	public void setObject(OntElement object) {
		this.object = object;
	}

}
