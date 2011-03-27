package unittest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import unittest.util.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { OntologyTestModule.class, VirtuosoModule.class })
public class OntologyResourceFacadeTest {
	@Inject
	private OntologyResourceFacade omdocResourceFacade;

	public OntologyResourceFacade getOmdocResourceFacade() {
		return omdocResourceFacade;
	}

	@Test
	public void testLoadArticleMetadataResource() {
		OntologyResource resource = new OntologyResource(
				"http://arxiv.org/abs/math/0205001v1");
		ArticleMetadata articleMetadata = getOmdocResourceFacade().load(
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
}
