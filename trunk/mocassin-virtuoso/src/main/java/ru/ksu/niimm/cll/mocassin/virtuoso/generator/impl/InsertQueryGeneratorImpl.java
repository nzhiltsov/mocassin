package ru.ksu.niimm.cll.mocassin.virtuoso.generator.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.query.QueryBuilder;
import ru.ksu.niimm.cll.mocassin.virtuoso.query.QueryBuilder.QueryType;

public class InsertQueryGeneratorImpl implements InsertQueryGenerator {

	@Override
	public String generate(List<RDFTriple> triples, RDFGraph graph) {
		QueryBuilder queryBuilder = new QueryBuilder(QueryType.INSERT);
		String graphIri = graph.getIri();
		return queryBuilder.addGraphUri(graphIri).addTriples(triples).build();

	}
}
