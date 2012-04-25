package ru.ksu.niimm.cll.mocassin.rdf.ontology.query;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.query.QueryBuilder.QueryType;


public class DescribeQueryGeneratorImpl implements DescribeQueryGenerator {

	@Override
	public String generate(String resourceUri) {
		QueryBuilder queryBuilder = new QueryBuilder(QueryType.DESCRIBE);
		return queryBuilder.addResourceUri(resourceUri).build();
	}
}
