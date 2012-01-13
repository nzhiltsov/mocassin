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
	 * update the given graph using a given expression
	 * 
	 * @param graph
	 * @param expression
	 */
	void executeUpdate(RDFGraph graph, String expression);

	/**
	 * delete all the triples that relate to a document with given URI
	 * @param graph
	 * @param documentUri
	 */
	void delete(RDFGraph graph, String documentUri);

	/**
	 * insert the triples into a given graph. <br/>
	 * Deprecated. Use {@link #insert(List)} instead
	 * @param graph
	 * @param triples
	 */
	void insert(RDFGraph graph, List<RDFTriple> triples);

	/**
	 * update the triples that relate to a document with given URI
	 * @param graph
	 * @param documentUri
	 * @param triples
	 */
	void update(RDFGraph graph, String documentUri, List<RDFTriple> triples);

	/**
	 * get resources by a given query
	 * @param graph
	 * @param query
	 * 
	 * @return
	 */
	List<QuerySolution> get(RDFGraph graph, Query query);

	/**
	 * get resources by a given query expression
	 * @param graph
	 * @param query
	 * @param isInferenceOn
	 * 
	 * @return
	 */
	List<QuerySolution> get(RDFGraph graph, String query, boolean isInferenceOn);

	/**
	 * get an RDF model that describes the resource with given URI
	 * @param graph
	 * @param resourceUri
	 *            resource URI
	 * 
	 * @return
	 */
	Model describe(RDFGraph graph, String resourceUri);

}
