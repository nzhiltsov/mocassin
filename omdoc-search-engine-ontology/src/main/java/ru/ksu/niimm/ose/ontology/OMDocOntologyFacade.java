package ru.ksu.niimm.ose.ontology;

import java.util.List;

public interface OMDocOntologyFacade {
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
	 * load individuals for given ontology concept
	 * 
	 * @param ontologyConcept
	 * @return
	 */
	List<OntologyIndividual> getIndividuals(OntologyConcept ontologyConcept);

}