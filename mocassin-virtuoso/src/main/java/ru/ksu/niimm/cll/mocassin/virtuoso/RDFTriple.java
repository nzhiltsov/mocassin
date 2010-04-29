package ru.ksu.niimm.cll.mocassin.virtuoso;

public interface RDFTriple {
	/**
	 * 
	 * return RDF triple representation in the N3 format, e.g.
	 * <p>
	 * 
	 * 
	 * <pre>
	 * < all.omdoc#whatislogic >	< http://www.w3.org/1999/02/22-rdf-syntax-ns#type >	< http://omdoc.org/ontology#Theory >
	 * </pre>
	 * 
	 * 
	 * @return RDF triple representation in the N3 format
	 */
	String getValue();
}
