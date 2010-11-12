package unittest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.ArticleMetadata;
import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocResourceFacade;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import unittest.util.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { OntologyTestModule.class, VirtuosoModule.class })
public class OMDocResourceFacadeTest {
	@Inject
	private OMDocResourceFacade omdocResourceFacade;

	public OMDocResourceFacade getOmdocResourceFacade() {
		return omdocResourceFacade;
	}

	@Test
	public void testLoadArticleMetadataResource() {
		OntologyResource resource = new OntologyResource(
				"all.omdoc#whatislogic");
		OMDocElement omdocElement = getOmdocResourceFacade().load(resource);
		ArticleMetadata articleMetadata = omdocElement.getArticleMetadata();
		Assert.assertEquals("all.omdoc", articleMetadata.getUri());
		boolean titleEquals = "Logic".equals(articleMetadata.getTitle())
				|| "Logic and something else"
						.equals(articleMetadata.getTitle());
		Assert.assertTrue(titleEquals);
		Assert.assertTrue(articleMetadata.getAuthors() != null);
		Assert.assertTrue(articleMetadata.getAuthors().contains("Author1"));
		Assert.assertTrue(articleMetadata.getAuthors().contains("Author2"));
	}
}
