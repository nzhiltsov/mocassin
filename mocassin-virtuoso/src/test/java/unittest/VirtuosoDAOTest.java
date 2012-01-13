package unittest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.util.IOUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

import com.google.inject.Inject;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class VirtuosoDAOTest extends AbstractTest {

	@Inject
	private VirtuosoDAO virtuosoDAO;

	private String largeInsertQuery;

	@Before
	public void initialize() throws IOException {
		ClassLoader classLoader = VirtuosoDAOTest.class.getClassLoader();
		largeInsertQuery = IOUtil.readContents(classLoader
				.getResource("LargeInsertQuery.sparql"));
	}

	@Test
	public void testInsert() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(
				"<http://linkeddata.tntbase.org/temp> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Document> .");
		triples.add(triple);

		getVirtuosoDAO().insert(getConfiguredGraph(), triples);
	}

	@Test
	public void testLargeUpdateExpression() {
		getVirtuosoDAO().executeUpdate(getConfiguredGraph(), largeInsertQuery);
	}

	@Test
	public void testDelete() {
		RDFGraph graph = getConfiguredGraph();
		getVirtuosoDAO().delete(graph, "all1.omdoc");
	}

	@Test
	public void testUpdate() {
		List<RDFTriple> triples = createTriplesForUpdate();

		triples.addAll(createTheoremTextTriples());

		RDFGraph graph = getConfiguredGraph();

		getVirtuosoDAO().update(graph, "all.omdoc", triples);

	}

	@Test
	public void testDescribe() {
		Model model = getVirtuosoDAO().describe(getGraph(),
				"<http://arxiv.org/abs/math/0005005v2>");
		Graph describeGraph = model.getGraph();
		ExtendedIterator<Triple> foundIt = describeGraph.find(Node.ANY,
				Node.ANY, Node.ANY);
		boolean contains = false;
		Node subject = Node.createURI("http://arxiv.org/abs/math/0005005v2");
		Node predicate = Node
				.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		Node object = Node
				.createURI("http://salt.semanticauthoring.org/ontologies/sdo#Publication");
		Triple tripleForSearch = new Triple(subject, predicate, object);
		while (foundIt.hasNext()) {
			Triple triple = foundIt.next();
			if (triple.equals(tripleForSearch)) {
				contains = true;
				break;
			}
		}
		Assert.assertTrue(contains);
	}

	private List<RDFTriple> createTheoremTextTriples() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple1 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1.pr1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Property> .");
		triples.add(triple1);
		RDFTriple triple2 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1> <http://omdoc.org/ontology#hasProperty> <all.omdoc#whatislogic.t1.pr1> .");
		triples.add(triple2);
		RDFTriple triple3 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1.pr1> <http://omdoc.org/ontology#hasText> \"Any effectively generated theory capable of expressing elementary arithmetic cannot be both consistent complete.\" .");
		triples.add(triple3);
		return triples;
	}

	private List<RDFTriple> createTriplesForUpdate() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(
				"<all.omdoc> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Document> .");
		triples.add(triple);
		RDFTriple triple2 = new RDFTripleImpl(
				"<all.omdoc> <http://omdoc.org/ontology#hasPart> <all.omdoc#whatislogic> .");
		triples.add(triple2);
		RDFTriple triple3 = new RDFTripleImpl(
				"<all.omdoc#whatislogic> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theory> .");
		triples.add(triple3);
		RDFTriple triple4 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.p1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Proof> .");
		triples.add(triple4);
		RDFTriple triple5 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theorem> .");
		triples.add(triple5);
		RDFTriple triple6 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.p1> <http://omdoc.org/ontology#proves> <all.omdoc#whatislogic.t1> .");
		triples.add(triple6);
		RDFTriple triple7 = new RDFTripleImpl(
				"<all.omdoc> <http://purl.org/dc/elements/1.1/title> \"Logic and something else\" .");
		triples.add(triple7);
		RDFTriple triple8 = new RDFTripleImpl(
				"<all.omdoc> <http://purl.org/dc/elements/1.1/creator> \"Author1\" .");
		triples.add(triple8);
		RDFTriple triple9 = new RDFTripleImpl(
				"<all.omdoc> <http://purl.org/dc/elements/1.1/creator> \"Author2\" .");
		triples.add(triple9);
		return triples;
	}

	@Test
	public void testGet() {
		RDFGraph graph = getConfiguredGraph();
		String query = "SELECT DISTINCT ?1 "
				+ "{ ?1 a <http://salt.semanticauthoring.org/ontologies/sdo#Publication> .}";
		List<QuerySolution> solutions = getVirtuosoDAO()
				.get(graph, query, true);
		for (QuerySolution solution : solutions) {
			Resource resource = solution.getResource("?1");
		}
	}

	public VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	private RDFGraph getConfiguredGraph() {
		return getGraph();
	}
}
