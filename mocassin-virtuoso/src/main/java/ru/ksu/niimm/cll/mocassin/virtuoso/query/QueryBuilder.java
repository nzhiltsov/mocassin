package ru.ksu.niimm.cll.mocassin.virtuoso.query;

import java.util.LinkedList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

/**
 * SPARQL query builder according to SPARQL/Update grammar
 * 
 * @see http://www.w3.org/Submission/SPARQL-Update
 * 
 * @author nzhiltsov
 * 
 */
public class QueryBuilder {
	private static final String DESCRIBE_EXPRESSION = "DESCRIBE %s FROM NAMED %s";
	private static final String INSERT_EXPRESSION = "INSERT INTO GRAPH <%s> {%s}";
	private static final String DELETE_EXPRESSION = "DELETE FROM %s {?s ?p ?o} WHERE {%s}";
	private final QueryType type;
	private String graphUri;
	private String documentUri;
	private String resourceUri;
	private List<RDFTriple> triples = new LinkedList<RDFTriple>();

	public enum QueryType {
		INSERT, DELETE, DESCRIBE
	}

	public QueryBuilder(QueryType type) {
		this.type = type;
	}

	/**
	 * add graph URI
	 * 
	 * @param uri
	 *            graph URI
	 * @return
	 */
	public QueryBuilder addGraphUri(String uri) {
		this.graphUri = uri;
		return this;
	}

	/**
	 * add RDF triple
	 * 
	 * @param triple
	 * @return
	 */
	public QueryBuilder addTriple(RDFTriple triple) {
		getTriples().add(triple);
		return this;
	}

	/**
	 * add document URI
	 * 
	 * @param documentUri
	 * @return
	 */
	public QueryBuilder addDocumentUri(String documentUri) {
		this.documentUri = documentUri;
		return this;
	}

	public QueryBuilder addResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
		return this;
	}

	/**
	 * add RDF triples
	 * 
	 * @param triples
	 * @return
	 */
	public QueryBuilder addTriples(List<RDFTriple> triples) {
		getTriples().addAll(triples);
		return this;
	}

	public String build() {
		if (!validate()) {
			throw new RuntimeException("builting query is invalid");
		}
		String query = "";
		switch (getType()) {
		case INSERT:
			query = buildInsertQuery();
			break;
		case DELETE:
			query = buildDeleteQuery();
			break;
		case DESCRIBE:
			query = buildDescribeQuery();
			break;
		default:
			throw new UnsupportedOperationException(String.format(
					"this operation isn't supported: %s", getType()));
		}
		return query;
	}

	private String buildDescribeQuery() {
		return String.format(DESCRIBE_EXPRESSION, getResourceUri(),
				getGraphUri());
	}

	private String buildDeleteQuery() {
		String whereClause = String.format(
				"?s ?p ?o. FILTER (regex(?s, \"^%s\") || regex(?o, \"^%s\"))",
				getDocumentUri(), getDocumentUri());
		return String.format(DELETE_EXPRESSION, getGraphUri(), whereClause);
	}

	private String buildInsertQuery() {
		StringBuffer constructTemplate = new StringBuffer();
		for (RDFTriple triple : getTriples()) {
			String tripleStr = String.format("%s\n", triple.getValue());
			constructTemplate.append(tripleStr);
		}
		return String.format(INSERT_EXPRESSION, getGraphUri(),
				constructTemplate);
	}

	private boolean validate() {
		switch (this.type) {
		case INSERT:
			return validateInsert();
		case DELETE:
			return validateDelete();
		case DESCRIBE:
			return validateDescribe();
		default:
			throw new UnsupportedOperationException(String.format(
					"this operation isn't supported: %s", this.type));
		}
	}

	private boolean validateDescribe() {
		if (!validateGraphUri())
			return false;
		if (!validateResourceUri())
			return false;
		return true;
	}

	private boolean validateResourceUri() {
		return !isEmpty(getResourceUri());
	}

	private boolean validateDelete() {
		if (!validateGraphUri())
			return false;
		if (!validateDocumentUri())
			return false;
		return true;
	}

	private boolean validateDocumentUri() {
		return !isEmpty(getDocumentUri());
	}

	private boolean validateGraphUri() {
		return !isEmpty(getGraphUri());
	}

	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	private boolean validateTriple(RDFTriple triple) {
		return !isEmpty(triple.getValue());
	}

	private boolean validateInsert() {
		if (!validateGraphUri())
			return false;
		for (RDFTriple triple : getTriples()) {
			if (!validateTriple(triple)) {
				return false;
			}
		}
		return true;
	}

	private QueryType getType() {
		return type;
	}

	private String getGraphUri() {
		return graphUri;
	}

	private List<RDFTriple> getTriples() {
		return triples;
	}

	private String getDocumentUri() {
		return documentUri;
	}

	private String getResourceUri() {
		return resourceUri;
	}

}
