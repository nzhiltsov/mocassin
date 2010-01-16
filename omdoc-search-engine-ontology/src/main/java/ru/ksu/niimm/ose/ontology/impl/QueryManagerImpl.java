package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.ose.ontology.OntologyElement;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import ru.ksu.niimm.ose.ontology.QueryManager;
import ru.ksu.niimm.ose.ontology.QueryStatement;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

public class QueryManagerImpl implements QueryManager {
	private static final String RDF_PREFIX_STRING = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private static final String SELECT_STATEMENT = "SELECT DISTINCT %s WHERE";
	private static final String RETRIEVED_CONCEPT_KEY = "?1";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.impl.QueryManager#query(com.hp.hpl.jena.ontology
	 * .OntModel, java.lang.String)
	 */
	public List<Resource> query(OntModel model, String queryString,
			String retrievedResourceKey) {
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet results = queryExecution.execSelect();
		List<Resource> resultResources = new ArrayList<Resource>();
		while (results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			Resource resource = solution.getResource(retrievedResourceKey);
			resultResources.add(resource);
		}
		return resultResources;
	}

	public List<OntologyResource> query(OntModel model,
			QueryStatement queryStatement) {
		List<OntologyResource> ontologyResources = new ArrayList<OntologyResource>();
		String queryString = generateQuery(queryStatement);
		List<Resource> resources = query(model, queryString,
				RETRIEVED_CONCEPT_KEY);
		for (Resource resource : resources) {
			OntologyResource ontologyResource = new OntologyResource(resource
					.getURI());
			ontologyResources.add(ontologyResource);
		}
		return ontologyResources;
	}

	private String generateQuery(QueryStatement queryStatement) {
		StringBuffer sb = new StringBuffer();
		sb.append(RDF_PREFIX_STRING);
		sb.append("\n");
		sb.append(String.format(SELECT_STATEMENT, RETRIEVED_CONCEPT_KEY));
		sb.append("\n");
		sb.append("{");
		sb.append("\n");
		List<OntologyTriple> retrievedTriples = queryStatement
				.getRetrievedTriples();
		int i = 1;
		// TODO : need to define the main retrieved concept and map each of
		// generated binding parameters to its retrieved concept
		for (OntologyTriple triple : retrievedTriples) {
			OntologyElement tripleObject = triple.getObject();
			boolean isIndividual = tripleObject instanceof OntologyIndividual;
			String tripleObjectString = isIndividual ? String.format("<%s>",
					tripleObject.getUri()) : String.format("?%d", i + 1);
			String whereClause = String.format(
					"?%d rdf:type <%s> . ?%d <%s> %s .", i, triple.getSubject()
							.getUri(), i, triple.getPredicate().getUri(),
					tripleObjectString);
			sb.append(whereClause);
			sb.append("\n");
			i++;
		}
		sb.append("}");
		return sb.toString();
	}

}
