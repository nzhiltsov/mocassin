package unittest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.ontology.SGEdge;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ OntologyTestModule.class, VirtuosoModule.class })
public class OntologyResourceFacadeTest {
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;
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
	public void testLoadArticleMetadataResource() {
		OntologyResource resource = new OntologyResource(
				"http://mathnet.ru/ivm537");
		ArticleMetadata articleMetadata = getOntologyResourceFacade().load(
				resource);
		Assert.assertEquals("Article id does not equal to the expected one.",
				"ivm537", articleMetadata.getCollectionId());
		boolean titleEquals = "Неточный комбинированный релаксационный метод для многозначных включений"
				.equals(articleMetadata.getTitle());

		Assert.assertTrue(titleEquals);
		Assert.assertEquals("Number of authors does not equal to the expected one.", 1, articleMetadata.getAuthors().size());
		Author singleAuthor = articleMetadata.getAuthors().get(0);
		Assert.assertEquals("И. В. Коннов", singleAuthor.getName());
		Assert.assertEquals(
				"Author's affiliation does not equal to the expected one.",
				"Казанский государственный университет",
				singleAuthor.getAffiliation());
	}

	@Test
	public void testInsert() {

		Set<RDFTriple> triples = new HashSet<RDFTriple>();
		triples.add(new RDFTripleImpl(
				"<http://mathnet.ru/ivm18/1> a <http://cll.niimm.ksu.ru/ontologies/mocassin#Theorem> ."));
		triples.add(new RDFTripleImpl(
				"<http://mathnet.ru/ivm18/2> a <http://cll.niimm.ksu.ru/ontologies/mocassin#Lemma> ."));
		triples.add(new RDFTripleImpl(
				"<http://mathnet.ru/ivm18/1> <http://cll.niimm.ksu.ru/ontologies/mocassin#refersTo> <http://mathnet.ru/ivm18/2> ."));
		triples.add(new RDFTripleImpl(
				"<http://mathnet.ru/ivm18> <http://cll.niimm.ksu.ru/ontologies/mocassin#hasSegment> <http://mathnet.ru/ivm18/1> ."));
		triples.add(new RDFTripleImpl(
				"<http://mathnet.ru/ivm18> <http://cll.niimm.ksu.ru/ontologies/mocassin#hasSegment> <http://mathnet.ru/ivm18/2> ."));
		triples.add(new RDFTripleImpl(
				"<http://mathnet.ru/ivm18/1> <http://cll.niimm.ksu.ru/ontologies/mocassin#hasStartPageNumber> 1 ."));
		triples.add(new RDFTripleImpl(
				"<http://mathnet.ru/ivm18/2> <http://cll.niimm.ksu.ru/ontologies/mocassin#hasStartPageNumber> 2 ."));
		Assert.assertTrue("Failed to insert data.", getOntologyResourceFacade()
				.insert(triples));
		final String expectedAuthorName = "И. В. Олемской";
		final String expectedTitle = "Конструирование явных методов типа Рунге–Кутта интегрирования систем специального вида";
		ArticleMetadata retrievedArticleMetadata = getOntologyResourceFacade()
				.load(new OntologyResource("http://mathnet.ru/ivm18"));
		Assert.assertEquals("Article id does not equal to the expected one.",
				"ivm18", retrievedArticleMetadata.getCollectionId());
		Assert.assertEquals(
				"The retrieved title does not equal to the expected one.",
				expectedTitle, retrievedArticleMetadata.getTitle());
		Assert.assertEquals("Number of authors does not equal to the expected one.", 1, retrievedArticleMetadata.getAuthors().size());
		Author singleAuthor = retrievedArticleMetadata.getAuthors().get(0);
		Assert.assertEquals(expectedAuthorName, singleAuthor.getName());
		Assert.assertEquals(
				"Author's affiliation does not equal to the expected one.",
				null,
				singleAuthor.getAffiliation());

		List<SGEdge> graph = getOntologyResourceFacade()
				.retrieveStructureGraph(
						new OntologyResource("http://mathnet.ru/ivm18"));
		checkGraph(graph, "http://mathnet.ru/ivm18/1",
				"http://mathnet.ru/ivm18/2", MocassinOntologyClasses.THEOREM,
				MocassinOntologyClasses.LEMMA,
				MocassinOntologyRelations.REFERS_TO);

	}

	@Test
	public void testRetrieveGraph() {
		List<SGEdge> edges = getOntologyResourceFacade()
				.retrieveStructureGraph(
						new OntologyResource("http://mathnet.ru/ivm537"));
		checkGraph(edges, "http://mathnet.ru/ivm537/1017",
				"http://mathnet.ru/ivm537/1442",
				MocassinOntologyClasses.SECTION,
				MocassinOntologyClasses.EQUATION,
				MocassinOntologyRelations.DEPENDS_ON);
	}

	private void checkGraph(List<SGEdge> edges, String targetSubjectUri,
			String targetObjectUri, MocassinOntologyClasses targetSubjectType,
			MocassinOntologyClasses targetObjectType,
			MocassinOntologyRelations targetRelationType) {
		Assert.assertTrue("Retrieved graph is empty", !edges.isEmpty());
		boolean found = false;
		for (SGEdge edge : edges) {
			OntologyIndividual subject = edge.getSubject();
			found = subject.getUri().equals(targetSubjectUri)
					&& subject.getType() == targetSubjectType
					&& MocassinOntologyRelations.fromUri(
							edge.getPredicate().getUri()).equals(
							targetRelationType)
					&& edge.getObject().getUri().equals(targetObjectUri)
					&& edge.getObject().getType() == targetObjectType;
			if (found)
				break;
		}
		Assert.assertTrue("The expected relation couldn't be found.", found);
	}

	public OntologyResourceFacade getOntologyResourceFacade() {
		return ontologyResourceFacade;
	}

}
