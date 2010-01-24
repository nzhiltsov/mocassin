package ru.ksu.niimm.ose.ontology;


public interface OMDocResourceFacade {
	/**
	 * load OMDoc element info for given resource
	 * 
	 * @param resource
	 *            resource
	 * @return
	 */
	OMDocElement load(OntologyResource resource);

}