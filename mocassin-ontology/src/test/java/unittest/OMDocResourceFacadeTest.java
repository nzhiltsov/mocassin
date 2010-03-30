package unittest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocResourceFacade;
import ru.ksu.niimm.ose.ontology.OntologyModule;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.SourceReference;
import ru.ksu.niimm.ose.ontology.impl.OMDocResourceFacadeImpl;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(OntologyModule.class)
public class OMDocResourceFacadeTest {
	@Inject
	private OMDocResourceFacade omdocResourceFacade;

	public OMDocResourceFacade getOmdocResourceFacade() {
		return omdocResourceFacade;
	}

	@Test
	public void testLoadLemmaResource() {
		// TODO : correct file path to make relative it and include files into
		// project
		OntologyResource resource = new OntologyResource(
				"/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/all.omdoc#reasonable-incons-refutable");
		OMDocElement omdocElement = getOmdocResourceFacade().load(resource);
		Assert
				.assertEquals("reasonable-incons-refutable", omdocElement
						.getId());
		SourceReference testSrcRef = new SourceReference();
		testSrcRef
				.setFileName("/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/reasonable-calculus.tex");
		testSrcRef.setLine(20);
		testSrcRef.setColumn(64);
		Assert.assertEquals(testSrcRef, omdocElement.getSrcRef());
		Assert
				.assertEquals(
						"/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/all.omdoc#reasonable-incons-refutable",
						omdocElement.getResourceUri());
		Assert
				.assertEquals(
						"/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/reasonable-calculus.pdf",
						omdocElement.getPdfFileName());
		Assert.assertEquals("What is Logic?", omdocElement.getArticleMetadata()
				.getTitle());
	}

	@Test
	public void testLoadLemmaProofStepResource() {
		OntologyResource resource = new OntologyResource(
				"/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/all.omdoc#reasonable-incons-refutable-pf");
		OMDocElement omdocElement = getOmdocResourceFacade().load(resource);
		Assert.assertEquals("reasonable-incons-refutable-pf", omdocElement
				.getId());
		SourceReference testSrcRef = new SourceReference();
		testSrcRef
				.setFileName("/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/reasonable-calculus.tex");
		testSrcRef.setLine(25);
		testSrcRef.setColumn(87);
		Assert.assertEquals(testSrcRef, omdocElement.getSrcRef());
		Assert
				.assertEquals(
						"/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/all.omdoc#reasonable-incons-refutable-pf",
						omdocElement.getResourceUri());
		Assert
				.assertEquals(
						"/home/nzhiltsov/projects/thirdparty/small_stex_collection/logic/en/reasonable-calculus.pdf",
						omdocElement.getPdfFileName());
	}
}
