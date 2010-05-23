package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DeleteQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DescribeQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.validation.ValidateGraph;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

public class VirtuosoDAOImpl implements VirtuosoDAO {
	@Inject
	InsertQueryGenerator insertQueryGenerator;
	@Inject
	DeleteQueryGenerator deleteQueryGenerator;
	@Inject
	DescribeQueryGenerator describeQueryGenerator;

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

	@Override
	@ValidateGraph
	public List<QuerySolution> get(Query query, RDFGraph graph) {
		List<QuerySolution> solutions = new ArrayList<QuerySolution>();

		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
				query, virtGraph);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			solutions.add(solution);
		}
		return solutions;
	}

	@Override
	@ValidateGraph
	public Graph describe(String resourceUri, RDFGraph graph) {
		String query = getDescribeQueryGenerator().generate(resourceUri, graph);
		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
				query, virtGraph);
		Model model = vqe.execDescribe();
		return model.getGraph();
	}

	public InsertQueryGenerator getInsertQueryGenerator() {
		return insertQueryGenerator;
	}

	public DeleteQueryGenerator getDeleteQueryGenerator() {
		return deleteQueryGenerator;
	}

	public DescribeQueryGenerator getDescribeQueryGenerator() {
		return describeQueryGenerator;
	}

	private void execute(VirtGraph virtGraph, String expression) {
		VirtuosoUpdateRequest request = VirtuosoUpdateFactory.create(
				expression, virtGraph);
		request.exec();
	}
}
