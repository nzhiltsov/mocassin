package ru.ksu.niimm.cll.mocassin.virtuoso;

public interface RDFTriple {
	/**
	 * 
	 * returns RDF triple representation in the N3 format, e.g.
	 * 
	 * <pre>
	 * &quot;&lt;all.omdoc#whatislogic&gt; &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#type&gt; &lt;http://omdoc.org/ontology#Theory&gt; .&quot;
	 * </pre>
	 * 
	 * @return RDF triple representation in the N3 format
	 */
	String getValue();
}
