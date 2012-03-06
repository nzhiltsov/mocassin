package unittest;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
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
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTriple;
import ru.ksu.niimm.cll.mocassin.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.ontology.QueryStatement;
import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;

import com.google.inject.Inject;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ OntologyTestModule.class})
public class QueryManagerFacadeTest {
	@Inject
	private QueryManagerFacade queryManagerFacade;
	@Inject
	private RepositoryProvider<Repository> repositoryProvider;

	@Before
	public void init() throws Exception {
		Repository repository = repositoryProvider.get();
		RepositoryConnection connection = repository.getConnection();
		final String context = "http://cll.niimm.ksu.ru/mocassinfortest";
		try {

			connection.add(getClass().getResourceAsStream("/testmetadata.rdf"),
					context, RDFFormat.N3, repository.getValueFactory()
							.createURI(context));
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
	public void testGenerateVirtuosoFullTextQuery() {
		QueryStatement queryStatement = makeFullTextStatement();
		String generatedQuery = getQueryManagerFacade().generateQuery(
				queryStatement);
		Assert.assertTrue(
				"The generated query does not contain the expected substring",
				generatedQuery
						.contains("<bif:contains> \"'кольца' AND 'поля'\""));
		System.out.println(generatedQuery);
	}

	@Test
	@Ignore("Refactoring to using LuceneSail is required for in-memory repository.")
	public void testExecuteFullTextQuery() {
		QueryStatement queryStatement = makeFullTextStatement();

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
	public void testGenerateQueryWithWildcards() {
		QueryStatement queryStatement = makeWildcardStatement();
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
	public void testDescribe() {
		String model = getQueryManagerFacade().describe(
				"http://mathnet.ru/ivm537/1017");
		Model jenaModel = ModelFactory.createDefaultModel();
		jenaModel.read(new StringReader(model), null);
		Graph describeGraph = jenaModel.getGraph();
		ExtendedIterator<Triple> foundIt = describeGraph.find(
				Node.createURI("http://mathnet.ru/ivm537/1017"),
				Node.createURI(MocassinOntologyRelations.DEPENDS_ON.getUri()),
				Node.createURI("http://mathnet.ru/ivm537/1442"));

		Assert.assertTrue(foundIt.hasNext());
	}

	private QueryStatement makeWildcardStatement() {
		List<OntologyTriple> triples = new ArrayList<OntologyTriple>();
		OntologyConcept subject = new OntologyConcept(
				MocassinOntologyClasses.getUri(MocassinOntologyClasses.SECTION),
				"Section");
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
				MocassinOntologyClasses.getUri(MocassinOntologyClasses.SECTION),
				"Section");
		subject.setId(1);
		OntologyRelation predicate = new OntologyRelation(
				MocassinOntologyRelations.DEPENDS_ON.getUri(), "dependsOn");
		predicate.setId(2);
		OntologyConcept object = new OntologyConcept(
				MocassinOntologyClasses
						.getUri(MocassinOntologyClasses.EQUATION),
				"Equation");
		object.setId(3);
		triples.add(new OntologyTriple(subject, predicate, object));
		OntologyConcept subject2 = new OntologyConcept(
				MocassinOntologyClasses.getUri(MocassinOntologyClasses.SECTION),
				"Section");
		subject2.setId(1);
		OntologyRelation predicate2 = new OntologyRelation(
				MocassinOntologyRelations.HAS_TEXT.getUri(), "hasText");
		predicate2.setId(4);
		OntologyLiteral object2 = new OntologyLiteral("кольца поля");
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

}
