package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DeleteQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DescribeQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.N3Util;
import ru.ksu.niimm.cll.mocassin.virtuoso.validation.ValidateGraph;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

import com.google.inject.Inject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class VirtuosoDAOImpl implements VirtuosoDAO {
	private static final String RULES_SET_ENTRY = "define input:inference \"%s\"\n";
	private static final int SPARQL_LINE_NUMBER_LIMIT = 1000;
	@Inject
	Logger logger;
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
		if (triples.size() > SPARQL_LINE_NUMBER_LIMIT) {
			int i = 0;
			while (i < triples.size() / SPARQL_LINE_NUMBER_LIMIT) {

				executeUpdate(virtGraph, graph, triples.subList(
						SPARQL_LINE_NUMBER_LIMIT * i + 1,
						SPARQL_LINE_NUMBER_LIMIT * (i + 1)));
				i++;
			}
			List<RDFTriple> subList = triples.subList(SPARQL_LINE_NUMBER_LIMIT
					* i + 1, triples.size() - 1);
			subList.add(triples.get(0));
			executeUpdate(virtGraph, graph, subList);
		} else {
			executeUpdate(virtGraph, graph, triples);
		}
	}

	@Override
	@ValidateGraph
	public void delete(String documentUri, RDFGraph graph) {
		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		String expression = getDeleteQueryGenerator().generate(documentUri,
				graph);
		throw new UnsupportedOperationException("not implemented yet");
		// execute(virtGraph, expression);
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
	public List<QuerySolution> get(String query, RDFGraph graph, boolean isInferenceOn) {
		List<QuerySolution> solutions = new ArrayList<QuerySolution>();

		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		Query jenaQuery = QueryFactory.create(query);
		jenaQuery.addGraphURI("http://cll.niimm.ksu.ru/mocassintest");
		String resultQuery;
		if (isInferenceOn) {
			String inferenceEntry = String.format(RULES_SET_ENTRY, graph.getInferenceRulesSetName());
			resultQuery = inferenceEntry + jenaQuery.toString();
		} else {
			resultQuery = jenaQuery.toString();
		}
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
				resultQuery, virtGraph);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			solutions.add(solution);
		}
		return solutions;
	}

	@Override
	@ValidateGraph
	public Model describe(String resourceUri, RDFGraph graph) {
		String query = getDescribeQueryGenerator().generate(resourceUri, graph);
		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
				query, virtGraph);
		Model model = vqe.execDescribe();
		return model;
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

	private void executeUpdate(VirtGraph virtGraph, RDFGraph graph,
			List<RDFTriple> triples) {
		String n3Expression = N3Util.getExpression(triples);
		try {
			Model model = ModelFactory.createDefaultModel();
			model.read(new StringReader(n3Expression), "", "N3");
			String insertExpression = getInsertQueryGenerator().generate(
					triples, graph);
			VirtuosoUpdateRequest request = VirtuosoUpdateFactory.create(
					insertExpression, virtGraph); // TODO: use the model defined above and virtGraph.getBulkUpdateHandler()
			request.exec();
			
		} catch (Exception e) {

			String message = String.format(
					"failed to update the RDF graph='%s' due to: %s",
					virtGraph.getGraphName(), e.getCause().getMessage());
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}
	}
}
