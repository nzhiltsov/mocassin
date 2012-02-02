package unittest;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyBlankNode;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyConcept;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyLiteral;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyRelation;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTriple;
import ru.ksu.niimm.cll.mocassin.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.ontology.QueryStatement;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ OntologyTestModule.class, VirtuosoModule.class })
public class QueryManagerFacadeTest {
	@Inject
	private QueryManagerFacade queryManagerFacade;
	@Inject
	private SparqlQueryLoader sparqlQueryLoader;
	@Inject
	private RepositoryProvider<Repository> repositoryProvider;

	@Before
	public void init() throws Exception {
		Repository repository = repositoryProvider.get();
		RepositoryConnection connection = repository.getConnection();
		try {
			connection.add(getClass().getResourceAsStream("/testmetadata.rdf"),
					"http://cll.niimm.ksu.ru/mocassinfortest", RDFFormat.N3);
		} finally {
			connection.close();
		}
	}

	@Test
	public void testQueryStatement() {
		QueryStatement queryStatement = makeInstanceStatement();
		List<OntologyResource> resources = getQueryManagerFacade().query(
				queryStatement);
		Assert.assertTrue("Retrieved instances list is empty.",
				resources.size() > 0);
		boolean found = false;
		for (OntologyResource resource : resources) {
			found = resource.getUri().equals("http://mathnet.ru/ivm537/1017");
			if (found) {
				break;
			}
		}
		Assert.assertTrue("The expected instance is not found.", found);
	}

	@Test
	public void testGenerateQuery() {
		QueryStatement queryStatement = makeInstanceStatement();
		String queryString = getQueryManagerFacade().generateQuery(
				queryStatement);
		// TODO : add more sophisticated check of equals to correct string
		Assert.assertTrue(queryString != null && !queryString.equals(""));
	}

	@Test
	public void testGenerateQueryWithWildcards() {
		QueryStatement queryStatement = makeWildcardStatement();
		String generatedQuery = getQueryManagerFacade().generateQuery(
				queryStatement);
		String expectedQuery = "SELECT DISTINCT ?1 WHERE {?1 a <http://omdoc.org/ontology#Theorem> .}";
		// TODO : add checking for equality of queries
	}

	@Test
	public void testGenerateFullTextQuery() {
		QueryStatement queryStatement = makeFullTextStatement();

		String queryString = getQueryManagerFacade().generateQuery(
				queryStatement);
		queryString.length();
	}

	private QueryStatement makeWildcardStatement() {
		List<OntologyTriple> triples = new ArrayList<OntologyTriple>();
		OntologyConcept subject = new OntologyConcept(
				"http://omdoc.org/ontology#Theorem", "theorem");
		subject.setId(1);
		OntologyBlankNode predicate = new OntologyBlankNode();
		predicate.setId(2);
		OntologyBlankNode object = new OntologyBlankNode();
		object.setId(3);
		OntologyTriple triple = new OntologyTriple(subject, predicate, object);
		triples.add(triple);

		return new QueryStatement(triples);
	}

	private QueryStatement makeFullTextStatement() {
		List<OntologyTriple> triples = new ArrayList<OntologyTriple>();
		OntologyConcept subject = new OntologyConcept(
				"http://omdoc.org/ontology#Theorem", "theorem");
		subject.setId(1);
		OntologyRelation predicate = new OntologyRelation(
				"http://omdoc.org/ontology#hasProperty", "hasProperty");
		predicate.setId(2);
		OntologyConcept object = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		object.setId(3);
		triples.add(new OntologyTriple(subject, predicate, object));
		OntologyConcept subject2 = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		subject2.setId(3);
		OntologyRelation predicate2 = new OntologyRelation(
				"http://omdoc.org/ontology#hasText", "hasText");
		predicate2.setId(4);
		OntologyLiteral object2 = new OntologyLiteral(
				"arithmetic AND consistent");
		object2.setId(5);
		triples.add(new OntologyTriple(subject2, predicate2, object2));
		return new QueryStatement(triples);
	}

	private QueryStatement makeInstanceStatement() {
		List<OntologyTriple> retrievedTriples = new ArrayList<OntologyTriple>();
		OntologyRelation predicate = new OntologyRelation(
				MocassinOntologyRelations.DEPENDS_ON.getUri(),
				MocassinOntologyRelations.DEPENDS_ON.toString());
		predicate.setId(3);
		OntologyConcept subject = new OntologyConcept(
				MocassinOntologyClasses.getUri(MocassinOntologyClasses.SECTION),
				"Section");
		subject.setId(1);
		OntologyConcept object = new OntologyConcept(
				MocassinOntologyClasses
						.getUri(MocassinOntologyClasses.EQUATION),
				"Equation");
		object.setId(5);
		retrievedTriples.add(new OntologyTriple(subject, predicate, object));

		QueryStatement queryStatement = new QueryStatement(retrievedTriples);
		return queryStatement;
	}

	public QueryManagerFacade getQueryManagerFacade() {
		return queryManagerFacade;
	}

	public SparqlQueryLoader getSparqlQueryLoader() {
		return sparqlQueryLoader;
	}

}
