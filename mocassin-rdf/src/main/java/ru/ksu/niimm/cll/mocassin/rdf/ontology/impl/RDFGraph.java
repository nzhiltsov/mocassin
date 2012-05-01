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
package ru.ksu.niimm.cll.mocassin.rdf.ontology.impl;

/**
 * Interface to aggregate information about target RDF graph
 * 
 * @author nzhiltsov
 * 
 */
public interface RDFGraph {
	/**
	 * 
	 * @return RDF graph IRI
	 */
	String getIri();

	/**
	 * 
	 * @return username for connection to a triple store
	 */
	String getUsername();

	/**
	 * 
	 * @return password for connection to a triple store
	 */
	String getPassword();

	/**
	 * 
	 * @return URL for connection to a triple store
	 */
	String getUrl();

	/**
	 * 
	 * @return URI of an inference rules set associated with the graph
	 */
	String getInferenceRulesSetName();
}
