package ru.ksu.niimm.cll.mocassin.ontology.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.UnsupportedRDFormatException;

import ru.ksu.niimm.cll.mocassin.ontology.OntologyBlankNode;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyElement;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyLiteral;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTriple;
import ru.ksu.niimm.cll.mocassin.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.ontology.QueryStatement;
import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DescribeQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.rdf.model.Model;

public class QueryManagerFacadeImpl implements QueryManagerFacade {
	private static final String RULES_SET_ENTRY = "define input:inference \"%s\"\n %s";
	private static final String RDF_PREFIX_STRING = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private static final String SELECT_STATEMENT = "SELECT DISTINCT %s WHERE\n{\n";
	private static final String RETRIEVED_CONCEPT_KEY = "?1";

	private final RepositoryProvider<Repository> repositoryProvider;

	private final Logger logger;

	private final DescribeQueryGenerator describeQueryGenerator;

	private RDFGraph searchGraph;

	private String ontologyRulesSetName;

	@Inject
	public QueryManagerFacadeImpl(Logger logger,
			RepositoryProvider<Repository> repositoryProvider,
			DescribeQueryGenerator describeQueryGenerator,
			@Named("connection.url") String connectionUrl,
			@Named("connection.user.name") String username,
			@Named("connection.user.password") String password,
			@Named("graph.iri") String graphIri,
			@Named("ontology.rules.set") String ontologyRuleSet) {
		this.describeQueryGenerator = describeQueryGenerator;
		this.logger = logger;
		this.repositoryProvider = repositoryProvider;
		this.searchGraph = new RDFGraphImpl.Builder(graphIri)
				.url(connectionUrl).username(username).password(password)
				.inferenceRulesSetName(ontologyRuleSet).build();
		this.ontologyRulesSetName = ontologyRuleSet;
	}

	private Repository getRepository() throws RepositoryException {
		return this.repositoryProvider.get();
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
		try {
			RepositoryConnection connection = getRepository().getConnection();
			if (queryStatement.isInferenceOn()) {
				queryString = String.format(RULES_SET_ENTRY,
						searchGraph.getInferenceRulesSetName(), queryString);
			}
			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, queryString);

			TupleQueryResult result = tupleQuery.evaluate();

			try {
				while (result.hasNext()) {
					BindingSet resource = result.next();
					OntologyResource ontologyResource = new OntologyResource(
							resource.getValue("1").stringValue());
					ontologyResources.add(ontologyResource);
				}

			} finally {
				result.close();
				connection.close();
			}
		} catch (Exception e) {
			logger.log(
					Level.SEVERE,
					String.format("Failed to execute a query due to %s. Empty result set will be returned.",
					e));
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
	public String describe(String resourceUri) {
		String query = getDescribeQueryGenerator().generate(resourceUri);
		try {
			RepositoryConnection connection = getRepository().getConnection();
			try {
				GraphQuery graphQuery = connection.prepareGraphQuery(
						QueryLanguage.SPARQL, query);
				GraphQueryResult graphQueryResult = graphQuery.evaluate();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				QueryResultIO.write(graphQueryResult, RDFFormat.RDFXML,
						outputStream);
				return outputStream.toString("utf8");
			} finally {
				connection.close();
			}
		} catch (Exception e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"Failed to execute a desribe query due to %s. Null string will be returned.",
							e.getCause()));
			return null;
		}
	}

	public RDFGraph getSearchGraph() {
		return this.searchGraph;
	}

	public DescribeQueryGenerator getDescribeQueryGenerator() {
		return describeQueryGenerator;
	}

}
