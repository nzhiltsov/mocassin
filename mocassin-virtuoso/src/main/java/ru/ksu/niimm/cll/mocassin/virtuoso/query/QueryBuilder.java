package ru.ksu.niimm.cll.mocassin.virtuoso.query;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFNode;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFNode.Type;

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
			String tripleStr = String.format("%s %s %s .", triple.getSubject()
					.getUri(), triple.getPredicate().getUri(), triple
					.getObject().getUri());
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

	private boolean validateNumeric(String str) {
		return Pattern.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$", str);
	}

	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	private boolean validateNode(RDFNode node) {
		if (node == null)
			return false;

		String nodeUri = node.getUri();
		Type nodeType = node.getType();
		if (isEmpty(nodeUri) || nodeType == null) {
			return false;
		}
		switch (nodeType) {
		case IRI_REFERENCE:
			return validateIRIReference(nodeUri);
		case RDF_LITERAL:
			return validateRDFLiteral(nodeUri);
		case NUMERIC_LITERAL:
			return validateNumeric(nodeUri);
		case BOOLEAN_LITERAL:
			return validateBoolean(nodeUri);
		case BLANK_NODE:
			return validateBlankNode(nodeUri);
		default:
			throw new IllegalArgumentException(String.format(
					"this type of RDF node isn't supported: %s", nodeType));
		}
	}

	private boolean validateIRIReference(String nodeUri) {
		// TODO : complete implementation
		return true;
	}

	private boolean validateBlankNode(String nodeUri) {
		// TODO :complete implementation
		return nodeUri.startsWith("_:");
	}

	private boolean validateRDFLiteral(String nodeUri) {
		// TODO complete implementation
		return true;
	}

	private boolean validateBoolean(String nodeUri) {
		return nodeUri.equals("true") || nodeUri.equals("false");
	}

	private boolean validateTriple(RDFTriple triple) {
		return validateNode(triple.getSubject())
				&& validateNode(triple.getPredicate())
				&& validateNode(triple.getObject());
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
