package ru.ksu.niimm.cll.mocassin.virtuoso.generator.impl;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DeleteQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.query.QueryBuilder;
import ru.ksu.niimm.cll.mocassin.virtuoso.query.QueryBuilder.QueryType;

public class DeleteQueryGeneratorImpl implements DeleteQueryGenerator {

	@Override
	public String generate(String documentUri, RDFGraph graph) {
		QueryBuilder queryBuilder = new QueryBuilder(QueryType.DELETE);
		String graphIri = graph.getIri();
		return queryBuilder.addGraphUri(graphIri).addDocumentUri(documentUri)
				.build();
	}

}
