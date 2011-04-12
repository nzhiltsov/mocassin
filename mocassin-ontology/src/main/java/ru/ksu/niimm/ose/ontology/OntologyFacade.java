package ru.ksu.niimm.ose.ontology;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

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

}