package ru.ksu.niimm.cll.mocassin.virtuoso;

import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;

/**
 * DAO for Virtuoso RDF store
 * 
 * @author nzhiltsov
 * 
 */
public interface VirtuosoDAO {
	/**
	 * delete all the triples that relate to document with given URI
	 * 
	 * @param documentUri
	 * @param graph
	 */
	void delete(String documentUri, RDFGraph graph);

	/**
	 * insert the triples into given graph
	 * 
	 * @param triples
	 * @param graph
	 */
	void insert(List<RDFTriple> triples, RDFGraph graph);

	/**
	 * update the triples that relate to document with given URI
	 * 
	 * @param documentUri
	 * @param triples
	 * @param graph
	 */
	void update(String documentUri, List<RDFTriple> triples, RDFGraph graph);

	/**
	 * get resources by given query
	 * 
	 * @param query
	 * @param graph
	 * @return
	 */
	List<QuerySolution> get(Query query, RDFGraph graph);
}
