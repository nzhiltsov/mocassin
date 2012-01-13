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
	public void insert(RDFGraph graph, List<RDFTriple> triples) {
		if (triples.size() > SPARQL_LINE_NUMBER_LIMIT) {
			int i = 0;
			while (i < triples.size() / SPARQL_LINE_NUMBER_LIMIT) {

				executeUpdate(graph, triples.subList(SPARQL_LINE_NUMBER_LIMIT
						* i + 1, SPARQL_LINE_NUMBER_LIMIT * (i + 1)));
				i++;
			}
			List<RDFTriple> subList = triples.subList(SPARQL_LINE_NUMBER_LIMIT
					* i + 1, triples.size() - 1);
			subList.add(triples.get(0));
			executeUpdate(graph, subList);
		} else {
			executeUpdate(graph, triples);
		}
	}

	@Override
	@ValidateGraph
	public void delete(RDFGraph graph, String documentUri) {
		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		String expression = getDeleteQueryGenerator().generate(documentUri,
				graph);
		throw new UnsupportedOperationException("not implemented yet");
		// execute(virtGraph, expression);
	}

	@Override
	@ValidateGraph
	public void update(RDFGraph graph, String documentUri,
			List<RDFTriple> triples) {
		delete(graph, documentUri);
		insert(graph, triples);
	}

	@Override
	@ValidateGraph
	public List<QuerySolution> get(RDFGraph graph, Query query) {
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
	public List<QuerySolution> get(RDFGraph graph, String query,
			boolean isInferenceOn) {
		List<QuerySolution> solutions = new ArrayList<QuerySolution>();

		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		Query jenaQuery = QueryFactory.create(query);
		if (!query.contains("bif:contains")) {// TODO: workaround for Virtuoso's
												// bug with full-text indexes
			jenaQuery.addGraphURI("http://cll.niimm.ksu.ru/mocassintest");
		}
		String resultQuery;
		if (isInferenceOn) {
			String inferenceEntry = String.format(RULES_SET_ENTRY,
					graph.getInferenceRulesSetName());
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
	public Model describe(RDFGraph graph, String resourceUri) {
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

	private void executeUpdate(RDFGraph graph, List<RDFTriple> triples) {
		String n3Expression = N3Util.getExpression(triples);
		String insertExpression = null;

		Model model = ModelFactory.createDefaultModel();
		model.read(new StringReader(n3Expression), "", "N3");
		insertExpression = getInsertQueryGenerator().generate(triples, graph);
		executeUpdate(graph, insertExpression);
	}

	@Override
	public void executeUpdate(RDFGraph graph, String expression) {
		VirtGraph virtGraph = new VirtGraph(graph.getUrl(),
				graph.getUsername(), graph.getPassword());
		VirtuosoUpdateRequest request = VirtuosoUpdateFactory.create(
				expression, virtGraph); // TODO: use the model
															// defined
		// above and
		// virtGraph.getBulkUpdateHandler()
		try {
			request.exec();
		} catch (Exception e) {
			String message = String.format(
					"failed to update the RDF graph='%s' due to: %s",
					graph.getIri(), e.getCause());
			logger.log(Level.SEVERE, message);
			logger.log(Level.INFO, expression);
			throw new RuntimeException(e);
		}
	}
}
