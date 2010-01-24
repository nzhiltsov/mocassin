package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.ose.ontology.OntologyElement;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import ru.ksu.niimm.ose.ontology.QueryManagerFacade;
import ru.ksu.niimm.ose.ontology.QueryStatement;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;
import ru.ksu.niimm.ose.ontology.loader.impl.OMDocOntologyLoaderImpl;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

public class QueryManagerFacadeImpl implements QueryManagerFacade {
	private static final String RDF_PREFIX_STRING = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private static final String SELECT_STATEMENT = "SELECT DISTINCT %s WHERE";
	private static final String RETRIEVED_CONCEPT_KEY = "?1";

	private OntModel ontology;
	private OMDocOntologyLoader ontologyLoader = new OMDocOntologyLoaderImpl();

	public QueryManagerFacadeImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.impl.QueryManager#query(com.hp.hpl.jena.ontology
	 * .OntModel, java.lang.String)
	 */
	public List<Resource> query(OntModel model, String queryString,
			String retrievedResourceKey) {
		model.loadImports();
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExecution = SparqlDLExecutionFactory.create(query,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.impl.QueryManager#query(com.hp.hpl.jena.ontology
	 * .OntModel, ru.ksu.niimm.ose.ontology.QueryStatement)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.impl.QueryManager#generateQuery(ru.ksu.niimm
	 * .ose.ontology.QueryStatement)
	 */
	public String generateQuery(QueryStatement queryStatement) {
		StringBuffer sb = new StringBuffer();
		sb.append(RDF_PREFIX_STRING);
		sb.append("\n");
		sb.append(String.format(SELECT_STATEMENT, RETRIEVED_CONCEPT_KEY));
		sb.append("\n");
		sb.append("{");
		sb.append("\n");
		List<OntologyTriple> retrievedTriples = queryStatement
				.getRetrievedTriples();
		for (OntologyTriple triple : retrievedTriples) {
			OntologyElement tripleObject = triple.getObject();
			String whereClause = "";
			String subjectTypeString = String.format("?%d rdf:type <%s>",
					triple.getSubject().getId(), triple.getSubject().getUri());
			boolean isIndividual = tripleObject instanceof OntologyIndividual;
			if (isIndividual) {
				String individualString = String.format("?%d <%s> <%s>", triple
						.getSubject().getId(), triple.getPredicate().getUri(),
						triple.getObject().getUri());
				whereClause = String.format("%s .\n %s.", subjectTypeString,
						individualString);
			} else {

				String objectTypeString = String
						.format("?%d rdf:type <%s>",
								triple.getObject().getId(), triple.getObject()
										.getUri());
				String predicateString = String.format("?%d <%s> ?%d", triple
						.getSubject().getId(), triple.getPredicate().getUri(),
						triple.getObject().getId());
				whereClause = String.format("%s .\n %s .\n %s.",
						subjectTypeString, objectTypeString, predicateString);
			}

			sb.append(whereClause);
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	public OntModel getOntology() {
		if (ontology == null) {
			this.ontology = getOntologyLoader().getOntology();
		}
		return ontology;
	}

	public OMDocOntologyLoader getOntologyLoader() {
		return ontologyLoader;
	}

}
