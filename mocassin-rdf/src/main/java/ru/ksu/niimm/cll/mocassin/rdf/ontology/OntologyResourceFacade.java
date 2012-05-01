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

import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;

public interface OntologyResourceFacade {
	/**
	 * load metadata info for an article with a given resource description
	 * 
	 * @param resource
	 *            resource
	 * @return null, if metadata is absent or couldn't be retrieved
	 */
	ArticleMetadata load(OntologyResource resource);

	List<ArticleMetadata> loadAll();

	/**
	 * load the structure graph for an article with a given resource description
	 * 
	 * @param resource
	 * @return
	 */
	List<SGEdge> retrieveStructureGraph(OntologyResource resource);

	/**
	 * 
	 * @param statements
	 * @return true, if the insertion was successful
	 */
	boolean insert(List<Statement> statements);

}
