package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.ose.ontology.OntologyBlankNode;
import ru.ksu.niimm.ose.ontology.OntologyElement;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyLiteral;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import ru.ksu.niimm.ose.ontology.QueryManagerFacade;
import ru.ksu.niimm.ose.ontology.QueryStatement;
import ru.ksu.niimm.ose.ontology.loader.ModulePropertiesLoader;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;
import ru.ksu.niimm.ose.ontology.loader.RDFGraphPropertiesLoader;

import com.google.inject.Inject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class QueryManagerFacadeImpl implements QueryManagerFacade {
	private static final String OMDOC_RULES_SET_PARAMETER_NAME = "omdoc.rules.set";
	private static final String RULES_SET_ENTRY = "define input:inference \"%s\"";
	private static final String RDF_PREFIX_STRING = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private static final String SELECT_STATEMENT = "SELECT DISTINCT %s WHERE";
	private static final String RETRIEVED_CONCEPT_KEY = "?1";
	@Inject
	private OMDocOntologyLoader ontologyLoader;
	@Inject
	private VirtuosoDAO virtuosoDAO;
	@Inject
	private RDFGraphPropertiesLoader graphPropertiesLoader;
	@Inject
	private ModulePropertiesLoader modulePropertiesLoader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.impl.QueryManager#query(com.hp.hpl.jena.ontology
	 * .OntModel, java.lang.String)
	 */
	public List<Resource> query(String queryString, String retrievedResourceKey) {
		List<Resource> resultResources = new ArrayList<Resource>();

		List<QuerySolution> solutions = getVirtuosoDAO().get(queryString,
				getGraph());
		for (QuerySolution solution : solutions) {
			Resource resource = solution.getResource(retrievedResourceKey);
			resultResources.add(resource);
		}
		return resultResources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.impl.QueryManager#query(ru.ksu.niimm.ose.ontology
	 * .QueryStatement)
	 */
	public List<OntologyResource> query(QueryStatement queryStatement) {
		List<OntologyResource> ontologyResources = new ArrayList<OntologyResource>();
		String queryString = generateQuery(queryStatement);
		List<Resource> resources = query(queryString, RETRIEVED_CONCEPT_KEY);
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
		if (queryStatement.isInferenceOn()) {
			sb.append(String.format(RULES_SET_ENTRY, getOmdocRulesSetName()));
			sb.append("\n");
		}
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
			boolean isLiteral = tripleObject instanceof OntologyLiteral;
			boolean isBlankNode = tripleObject instanceof OntologyBlankNode;
			if (isIndividual) {
				String individualString = String.format("?%d <%s> <%s>", triple
						.getSubject().getId(), triple.getPredicate().getUri(),
						triple.getObject().getUri());
				whereClause = String.format("%s .\n %s.", subjectTypeString,
						individualString);
			} else if (isLiteral) {
				String containsString = String.format(
						"?%d <bif:contains> \"%s\"",
						triple.getObject().getId(), triple.getObject()
								.getLabel());
				String predicateString = getPredicateExpression(triple);
				whereClause = String.format("%s .\n %s .\n %s.",
						subjectTypeString, predicateString, containsString);
			} else if (isBlankNode) {
				boolean isPredicateBlankNode = triple.getPredicate() instanceof OntologyBlankNode;

				whereClause = isPredicateBlankNode ? String.format("%s .\n",
						subjectTypeString) : String.format("%s .\n %s .\n",
						subjectTypeString, getPredicateExpression(triple));
			} else {
				String objectTypeString = String
						.format("?%d rdf:type <%s>",
								triple.getObject().getId(), triple.getObject()
										.getUri());
				String predicateString = getPredicateExpression(triple);
				whereClause = String.format("%s .\n %s .\n %s.",
						subjectTypeString, objectTypeString, predicateString);
			}

			sb.append(whereClause);
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	private String getOmdocRulesSetName() {
		return getModulePropertiesLoader().get(OMDOC_RULES_SET_PARAMETER_NAME);
	}

	private String getPredicateExpression(OntologyTriple triple) {
		String predicateString = String.format("?%d <%s> ?%d", triple
				.getSubject().getId(), triple.getPredicate().getUri(), triple
				.getObject().getId());
		return predicateString;
	}

	@Override
	public Model describe(String resourceUri) {
		String uri = String.format("<%s>", resourceUri);
		return getVirtuosoDAO().describe(uri, getGraph());
	}

	public OMDocOntologyLoader getOntologyLoader() {
		return ontologyLoader;
	}

	public VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	public RDFGraphPropertiesLoader getGraphPropertiesLoader() {
		return graphPropertiesLoader;
	}

	public RDFGraph getGraph() {
		return getGraphPropertiesLoader().getGraph();
	}

	public ModulePropertiesLoader getModulePropertiesLoader() {
		return modulePropertiesLoader;
	}

}
