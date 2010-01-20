package ru.ksu.niimm.ose.ontology;

import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public interface QueryManager {
	/**
	 * query given model with given query string
	 * 
	 * @param model
	 *            ontology model
	 * @param queryString
	 *            query string
	 * @param retrievedResourceKey
	 *            retrieved resource key
	 */
	List<Resource> query(OntModel model, String queryString,
			String retrievedResourceKey);

	/**
	 * query given model with given query statement
	 * 
	 * @param model
	 *            ontology model
	 * @param queryStatement
	 *            query statement
	 * @return
	 */
	List<OntologyResource> query(OntModel model, QueryStatement queryStatement);

	/**
	 * generate SPARQL query for given query statement
	 * 
	 * @param queryStatement
	 *            query statement
	 * @return
	 */
	String generateQuery(QueryStatement queryStatement);

}