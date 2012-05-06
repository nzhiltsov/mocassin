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

public interface QueryManagerFacade {

	/**
	 * query given model with given query statement
	 * 
	 * @param queryStatement
	 *            query statement
	 * @return
	 */
	List<OntologyResource> query(QueryStatement queryStatement);

	/**
	 * generate SPARQL query for given query statement
	 * 
	 * @param queryStatement
	 *            query statement
	 * @return
	 */
	String generateQuery(QueryStatement queryStatement);

	/**
	 * get RDF description of resource with given URI as a string
	 * 
	 * @param resourceUri
	 *            resource URI, e.g.
	 *            "http://linkeddata.tntbase.org/slides/atp0/en/resolution-complete#resolution-disjunction-lemma"
	 * @return
	 */
	String describe(String resourceUri);
}
