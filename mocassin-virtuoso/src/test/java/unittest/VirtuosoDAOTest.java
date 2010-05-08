package unittest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

import com.google.inject.Inject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Resource;

public class VirtuosoDAOTest extends AbstractTest {

	@Inject
	private VirtuosoDAO virtuosoDAO;

	@Test
	public void testInsert() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(
				"<all1.omdoc#whatislogic> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theory>");
		triples.add(triple);
		RDFGraph graph = getConfiguredGraph();

		getVirtuosoDAO().insert(triples, graph);
	}

	@Test
	public void testDelete() {
		RDFGraph graph = getConfiguredGraph();
		getVirtuosoDAO().delete("all1.omdoc", graph);
	}

	@Test
	public void testUpdate() {
		List<RDFTriple> triples = createTriplesForUpdate();

		triples.addAll(createTheoremTextTriples());

		RDFGraph graph = getConfiguredGraph();

		getVirtuosoDAO().update("all.omdoc", triples, graph);

	}

	private List<RDFTriple> createTheoremTextTriples() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple1 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1.pr1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Property>");
		triples.add(triple1);
		RDFTriple triple2 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1> <http://omdoc.org/ontology#hasProperty> <all.omdoc#whatislogic.t1.pr1>");
		triples.add(triple2);
		RDFTriple triple3 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1.pr1> <http://omdoc.org/ontology#hasText> \"Any effectively generated theory capable of expressing elementary arithmetic cannot be both consistent complete.\"");
		triples.add(triple3);
		return triples;
	}

	private List<RDFTriple> createTriplesForUpdate() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(
				"<all.omdoc> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Document>");
		triples.add(triple);
		RDFTriple triple2 = new RDFTripleImpl(
				"<all.omdoc> <http://omdoc.org/ontology#hasPart> <all.omdoc#whatislogic>");
		triples.add(triple2);
		RDFTriple triple3 = new RDFTripleImpl(
				"<all.omdoc#whatislogic> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theory>");
		triples.add(triple3);
		RDFTriple triple4 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.p1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Proof>");
		triples.add(triple4);
		RDFTriple triple5 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.t1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theorem>");
		triples.add(triple5);
		RDFTriple triple6 = new RDFTripleImpl(
				"<all.omdoc#whatislogic.p1> <http://omdoc.org/ontology#proves> <all.omdoc#whatislogic.t1>");
		triples.add(triple6);
		RDFTriple triple7 = new RDFTripleImpl(
				"<all.omdoc> <http://purl.org/dc/elements/1.1/title> \"Logic and something else\"");
		triples.add(triple7);
		RDFTriple triple8 = new RDFTripleImpl(
				"<all.omdoc> <http://purl.org/dc/elements/1.1/creator> \"Author1\"");
		triples.add(triple8);
		RDFTriple triple9 = new RDFTripleImpl(
				"<all.omdoc> <http://purl.org/dc/elements/1.1/creator> \"Author2\"");
		triples.add(triple9);
		return triples;
	}

	@Test
	public void testGet() {
		RDFGraph graph = getConfiguredGraph();
		Query query = QueryFactory.create("SELECT * {?s ?p ?o} limit 100");
		List<QuerySolution> solutions = getVirtuosoDAO().get(query, graph);
		for (QuerySolution solution : solutions) {
			Resource resource = solution.getResource("?s");
		}
	}

	public VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	private RDFGraph getConfiguredGraph() {
		RDFGraph graph = new RDFGraphImpl.Builder(getProperties().getProperty(
				"graph.iri")).username(
				getProperties().getProperty("connection.user.name")).password(
				getProperties().getProperty("connection.user.password")).url(
				getProperties().getProperty("connection.url")).build();
		return graph;
	}
}
