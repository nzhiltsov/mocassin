package ru.ksu.niimm.ose.ontology;


public interface OMDocResourceLoader {
	/**
	 * load OMDoc element info for given resource
	 * 
	 * @param resource
	 *            resource
	 * @return
	 */
	OMDocElement load(OntologyResource resource);

}