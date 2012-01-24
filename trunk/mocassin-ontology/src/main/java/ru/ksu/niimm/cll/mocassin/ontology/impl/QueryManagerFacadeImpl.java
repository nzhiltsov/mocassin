package ru.ksu.niimm.cll.mocassin.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.ontology.OntologyBlankNode;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyElement;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyLiteral;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTriple;
import ru.ksu.niimm.cll.mocassin.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.ontology.QueryStatement;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class QueryManagerFacadeImpl implements QueryManagerFacade {
	private static final String RULES_SET_ENTRY = "define input:inference \"%s\"";
	private static final String RDF_PREFIX_STRING = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private static final String SELECT_STATEMENT = "SELECT DISTINCT %s WHERE\n{\n";
	private static final String RETRIEVED_CONCEPT_KEY = "?1";

	private VirtuosoDAO virtuosoDAO;

	private RDFGraph searchGraph;

	private String ontologyRulesSetName;

	@Inject
	public QueryManagerFacadeImpl(VirtuosoDAO virtuosoDAO,
			@Named("connection.url") String connectionUrl,
			@Named("connection.user.name") String username,
			@Named("connection.user.password") String password,
			@Named("graph.iri") String graphIri,
			@Named("ontology.rules.set") String ontologyRuleSet) {
		this.virtuosoDAO = virtuosoDAO;
		this.searchGraph = new RDFGraphImpl.Builder(graphIri)
				.url(connectionUrl).username(username).password(password)
				.inferenceRulesSetName(ontologyRuleSet).build();
		this.ontologyRulesSetName = ontologyRuleSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.impl.QueryManager#query(com.hp.hpl.jena.ontology
	 * .OntModel, java.lang.String)
	 */
	public List<Resource> query(QueryStatement queryStatement,
			String retrievedResourceKey) {
		List<Resource> resultResources = new ArrayList<Resource>();
		String queryString = generateQuery(queryStatement);
		List<QuerySolution> solutions = getVirtuosoDAO().get(getSearchGraph(),
				queryString, queryStatement.isInferenceOn());
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

		List<Resource> resources = query(queryStatement, RETRIEVED_CONCEPT_KEY);
		for (Resource resource : resources) {
			OntologyResource ontologyResource = new OntologyResource(
					resource.getURI());
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
		sb.append(String.format(SELECT_STATEMENT, RETRIEVED_CONCEPT_KEY));
		List<OntologyTriple> retrievedTriples = queryStatement
				.getRetrievedTriples();
		for (OntologyTriple triple : retrievedTriples) {
			OntologyElement tripleObject = triple.getObject();
			String whereClause = "";
			String subjectTypeString = String.format("?%d a <%s>", triple
					.getSubject().getId(), triple.getSubject().getUri());
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
				String objectTypeString = String.format("?%d a <%s>", triple
						.getObject().getId(), triple.getObject().getUri());
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

	private String getOntologyRulesSetName() {
		return this.ontologyRulesSetName;
	}

	private static String getPredicateExpression(OntologyTriple triple) {
		String predicateString = String.format("?%d <%s> ?%d", triple
				.getSubject().getId(), triple.getPredicate().getUri(), triple
				.getObject().getId());
		return predicateString;
	}

	@Override
	public Model describe(String resourceUri) {
		String uri = String.format("<%s>", resourceUri);
		return getVirtuosoDAO().describe(getSearchGraph(), uri);
	}

	public VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	public RDFGraph getSearchGraph() {
		return this.searchGraph;
	}

}
