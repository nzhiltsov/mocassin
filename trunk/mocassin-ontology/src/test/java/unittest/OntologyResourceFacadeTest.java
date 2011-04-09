package unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import unittest.util.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { OntologyTestModule.class, VirtuosoModule.class })
public class OntologyResourceFacadeTest {
	@Inject
	private OntologyResourceFacade omdocResourceFacade;

	public OntologyResourceFacade getOntologyResourceFacade() {
		return omdocResourceFacade;
	}

	@Test
	public void testLoadArticleMetadataResource() {
		OntologyResource resource = new OntologyResource(
				"http://arxiv.org/abs/math/0205001v1");
		ArticleMetadata articleMetadata = getOntologyResourceFacade().load(
				resource);
		Assert.assertEquals("http://arxiv.org/abs/math/0205001v1",
				articleMetadata.getId());
		boolean titleEquals = "A note on the Gurov-Reshetnyak condition"
				.equals(articleMetadata.getTitle());

		Assert.assertTrue(titleEquals);
		Assert.assertTrue(articleMetadata.getAuthors() != null);
	}

	@Test
	public void testInsertArticleMetadata() {
		// TODO: add body of the test
	}
	@Test
	public void testRetrieveGraph() {
		List<OntologyTriple> triples = getOntologyResourceFacade()
				.retrieveStructureGraph(
						new OntologyResource("http://arxiv.org/abs/1104.1182v1"));
		Assert.assertTrue(!triples.isEmpty());
		boolean found = false;
		for (OntologyTriple triple : triples) {
			found = triple.getSubject().getUri().equals(
					"http://arxiv.org/abs/1104.1182v1/s1459_1")
					&& triple.getPredicate().getUri().contains("refersTo")
					&& triple.getObject().getUri().equals(
							"http://arxiv.org/abs/1104.1182v1/s1373_1");
			if (found)
				break;
		}
		Assert.assertTrue(found);
	}
}
