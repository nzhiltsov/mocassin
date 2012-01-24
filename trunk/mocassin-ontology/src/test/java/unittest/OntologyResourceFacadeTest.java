package unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyModule;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.ontology.SGEdge;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ OntologyModule.class, VirtuosoModule.class })
public class OntologyResourceFacadeTest {
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;

	public OntologyResourceFacade getOntologyResourceFacade() {
		return ontologyResourceFacade;
	}

	@Test
	public void testLoadArticleMetadataResource() {
		OntologyResource resource = new OntologyResource(
				"http://mathnet.ru/ivm537");
		ArticleMetadata articleMetadata = getOntologyResourceFacade().load(
				resource);
		Assert.assertEquals("Article id does not equal to the expected one.", "ivm537", articleMetadata.getCollectionId());
		boolean titleEquals = "Неточный комбинированный релаксационный метод для многозначных включений"
				.equals(articleMetadata.getTitle());

		Assert.assertTrue(titleEquals);
		Assert.assertEquals(1, articleMetadata.getAuthors().size());
		Assert.assertEquals("И. В. Коннов ", articleMetadata.getAuthors()
				.get(0).getName());
	}

	@Test
	public void testInsertArticleMetadata() {
		// TODO: add body of the test
	}

	@Test
	public void testRetrieveGraph() {
		List<SGEdge> edges = getOntologyResourceFacade()
				.retrieveStructureGraph(
						new OntologyResource("http://mathnet.ru/ivm18"));
		Assert.assertTrue(!edges.isEmpty());
		boolean found = false;
		for (SGEdge edge : edges) {
			OntologyIndividual subject = edge.getSubject();
			found = subject.getUri().equals("http://mathnet.ru/ivm18/1017")
					&& subject.getType() == MocassinOntologyClasses.SECTION
					&& edge.getPredicate().getUri().contains("dependsOn")
					&& edge.getObject().getUri()
							.equals("http://mathnet.ru/ivm18/1442")
					&& edge.getObject().getType() == MocassinOntologyClasses.EQUATION;
			if (found)
				break;
		}
		Assert.assertTrue(found);
	}
}
