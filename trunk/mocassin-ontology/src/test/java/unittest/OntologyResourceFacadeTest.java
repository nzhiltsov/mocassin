package unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyModule;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import ru.ksu.niimm.ose.ontology.SGEdge;

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
		Assert.assertEquals("ivm537", articleMetadata.getCollectionId());
		boolean titleEquals = "Неточный комбинированный релаксационный метод для многозначных включений"
				.equals(articleMetadata.getTitle());

		Assert.assertTrue(titleEquals);
		Assert.assertEquals(1, articleMetadata.getAuthors().size());
		Assert.assertEquals("И. В. Коннов ", articleMetadata.getAuthors().get(0)
				.getName());
	}

	@Test
	public void testInsertArticleMetadata() {
		// TODO: add body of the test
	}

	@Test
	public void testRetrieveGraph() {
		List<SGEdge> edges = getOntologyResourceFacade()
				.retrieveStructureGraph(
						new OntologyResource(
								"http://arxiv.org/abs/math/0005005v2"));
		Assert.assertTrue(!edges.isEmpty());
		boolean found = false;
		for (SGEdge edge : edges) {
			OntologyIndividual subject = edge.getSubject();
			found = subject.getUri().equals(
					"http://arxiv.org/abs/math/0005005v2/1208")
					&& subject.getType() == MocassinOntologyClasses.PROOF
					&& edge.getPredicate().getUri().contains("dependsOn")
					&& edge.getObject().getUri()
							.equals("http://arxiv.org/abs/math/0005005v2/636")
					&& edge.getFromNumPage() == 2 && edge.getToNumPage() == 2;
			if (found)
				break;
		}
		Assert.assertTrue(found);
	}
}
