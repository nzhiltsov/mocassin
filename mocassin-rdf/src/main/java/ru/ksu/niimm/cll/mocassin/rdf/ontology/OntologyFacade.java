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


public interface OntologyFacade {
	/**
	 * receive list of all ontology concepts
	 * 
	 * @return
	 */
	List<OntologyConcept> getOntClassList();

	/**
	 * receive list of relations for given concept
	 * 
	 * @param ontologyConcept
	 *            concept
	 * @return
	 */
	List<OntologyRelation> getOntPropertyList(OntologyConcept ontologyConcept);

	/**
	 * receive list of concepts each of which is valid as a range of given
	 * relation
	 * 
	 * @param relation
	 *            relation
	 * @return
	 */
	List<OntologyConcept> getOntPropertyRangeList(OntologyRelation relation);

	/**
	 * For a given hierarchy of classes as a list returns the most specific class.
	 * Throws {@link IllegalArgumentException}, if given classes don't form a
	 * hierarchy.
	 * 
	 * @return
	 */
	MocassinOntologyClasses getMostSpecific(List<MocassinOntologyClasses> hierarchy);
	
	MocassinOntologyClasses getMoreSpecific(MocassinOntologyClasses first, MocassinOntologyClasses second);


    /**
     * Return list of relations that are valid on a given two classes
     *
     * @param first
     * @param second
     * @return
     */
    List<MocassinOntologyRelations> getRelations(MocassinOntologyClasses first, MocassinOntologyClasses second);

}
