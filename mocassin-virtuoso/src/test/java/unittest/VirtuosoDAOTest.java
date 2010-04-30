package unittest;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;
import unittest.util.LoadPropertiesUtil;

import com.google.inject.Inject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Resource;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

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

		RDFGraph graph = getConfiguredGraph();

		getVirtuosoDAO().update("all.omdoc", triples, graph);

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
		return triples;
	}

	@Test
	public void testGet() {
		RDFGraph graph = getConfiguredGraph();
		Query query = QueryFactory.create("SELECT * {?s ?p ?o}");
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
