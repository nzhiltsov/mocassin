package ru.ksu.niimm.cll.mocassin.virtuoso;

import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * DAO for Virtuoso RDF store
 * 
 * @author nzhiltsov
 * 
 */
public interface VirtuosoDAO {
	/**
	 * delete all the triples that relate to a document with given URI
	 * 
	 * @param documentUri
	 * @param graph
	 */
	void delete(String documentUri, RDFGraph graph);

	/**
	 * insert the triples into a given graph. <br/>
	 * Deprecated. Use {@link #insert(List)} instead
	 * 
	 * @param triples
	 * @param graph
	 */
	void insert(List<RDFTriple> triples, RDFGraph graph);

	/**
	 * update the triples that relate to a document with given URI
	 * 
	 * @param documentUri
	 * @param triples
	 * @param graph
	 */
	void update(String documentUri, List<RDFTriple> triples, RDFGraph graph);

	/**
	 * get resources by a given query
	 * 
	 * @param query
	 * @param graph
	 * @return
	 */
	List<QuerySolution> get(Query query, RDFGraph graph);

	/**
	 * get resources by a given query expression
	 * 
	 * @param query
	 * @param graph
	 * @return
	 */
	List<QuerySolution> get(String query, RDFGraph graph);

	/**
	 * get an RDF model that describes the resource with given URI
	 * 
	 * @param resourceUri
	 *            resource URI
	 * @param graph
	 * @return
	 */
	Model describe(String resourceUri, RDFGraph graph);
}
