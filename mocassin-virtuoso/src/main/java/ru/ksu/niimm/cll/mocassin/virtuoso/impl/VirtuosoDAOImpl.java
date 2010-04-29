package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import java.util.List;

import com.google.inject.Inject;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DeleteQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.validation.ValidateGraph;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

public class VirtuosoDAOImpl implements VirtuosoDAO {
	@Inject
	InsertQueryGenerator insertQueryGenerator;
	@Inject
	DeleteQueryGenerator deleteQueryGenerator;

	@Override
	@ValidateGraph
	public void insert(List<RDFTriple> triples, RDFGraph graph) {
		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		String expression = getInsertQueryGenerator().generate(triples, graph);
		execute(virtGraph, expression);

	}

	@Override
	@ValidateGraph
	public void delete(String documentUri, RDFGraph graph) {
		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		String expression = getDeleteQueryGenerator().generate(documentUri,
				graph);
		execute(virtGraph, expression);
	}

	@Override
	@ValidateGraph
	public void update(String documentUri, List<RDFTriple> triples,
			RDFGraph graph) {
		delete(documentUri, graph);
		insert(triples, graph);
	}

	public InsertQueryGenerator getInsertQueryGenerator() {
		return insertQueryGenerator;
	}

	public DeleteQueryGenerator getDeleteQueryGenerator() {
		return deleteQueryGenerator;
	}

	private void execute(VirtGraph virtGraph, String expression) {
		VirtuosoUpdateRequest request = VirtuosoUpdateFactory.create(
				expression, virtGraph);
		request.exec();
	}
}
