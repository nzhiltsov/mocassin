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

public class OntologyLiteral extends OntologyElement {

	private static final String RDFS_LITERAL_URI = "http://www.w3.org/2000/01/rdf-schema#Literal";

	public OntologyLiteral(String label) {
		super(RDFS_LITERAL_URI, label);
	}

}
