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
	private static final String INSERT_EXPRESSION = "INSERT INTO GRAPH %s {%s}";
	private final QueryType type;
	private String graphUri;
	private List<RDFTriple> triples = new LinkedList<RDFTriple>();

	public enum QueryType {
		INSERT
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
		default:
			new UnsupportedOperationException(String.format(
					"this operation isn't supported: %s", this.type));
		}
		return query;
	}

	private String buildInsertQuery() {
		StringBuffer constructTemplate = new StringBuffer();
		for (RDFTriple triple : getTriples()) {
			String tripleStr = String.format("%s .", triple.getValue());
			constructTemplate.append(tripleStr);
		}
		return String.format(INSERT_EXPRESSION, getGraphUri(),
				constructTemplate);
	}

	private boolean validate() {
		switch (this.type) {
		case INSERT:
			return validateInsert();
		default:
			throw new UnsupportedOperationException(String.format(
					"this operation isn't supported: %s", this.type));
		}
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

}
