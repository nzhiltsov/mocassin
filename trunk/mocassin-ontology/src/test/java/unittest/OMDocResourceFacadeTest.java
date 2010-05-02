package unittest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.ArticleMetadata;
import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocResourceFacade;
import ru.ksu.niimm.ose.ontology.OntologyModule;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.SourceReference;
import ru.ksu.niimm.ose.ontology.impl.OMDocResourceFacadeOldImpl;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { OntologyModule.class, VirtuosoModule.class })
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
		Assert.assertEquals("Logic", articleMetadata.getTitle());
	}
}
