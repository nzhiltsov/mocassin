package ru.ksu.niimm.ose.ontology;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public interface QueryManagerFacade {
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
	List<Resource> query(String queryString, String retrievedResourceKey);

	/**
	 * query given model with given query statement
	 * 
	 * @param queryStatement
	 *            query statement
	 * @return
	 */
	List<OntologyResource> query(QueryStatement queryStatement);

	/**
	 * generate SPARQL query for given query statement
	 * 
	 * @param queryStatement
	 *            query statement
	 * @return
	 */
	String generateQuery(QueryStatement queryStatement);

	/**
	 * get RDF description of resource with given URI
	 * 
	 * @param resourceUri
	 *            resource URI, e.g."http://linkeddata.tntbase.org/slides/atp0/en/resolution-complete#resolution-disjunction-lemma"
	 * @return
	 */
	Model describe(String resourceUri);
}