package ru.ksu.niimm.cll.mocassin.virtuoso.generator.impl;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DescribeQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.query.QueryBuilder;
import ru.ksu.niimm.cll.mocassin.virtuoso.query.QueryBuilder.QueryType;

public class DescribeQueryGeneratorImpl implements DescribeQueryGenerator {

	@Override
	public String generate(String resourceUri, RDFGraph graph) {
		QueryBuilder queryBuilder = new QueryBuilder(QueryType.DESCRIBE);
		String graphIri = graph.getIri();
		return queryBuilder.addResourceUri(resourceUri).addGraphUri(graphIri)
				.build();
	}

	@Override
	public String generate(String resourceUri) {
		QueryBuilder queryBuilder = new QueryBuilder(QueryType.DESCRIBE);
		return queryBuilder.addResourceUri(resourceUri).build();
	}
}
