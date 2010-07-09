package ru.ksu.niimm.cll.mocassin.virtuoso;

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
}
