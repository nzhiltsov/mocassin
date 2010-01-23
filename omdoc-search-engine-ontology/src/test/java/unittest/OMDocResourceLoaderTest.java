package unittest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocResourceLoader;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.SourceReference;
import ru.ksu.niimm.ose.ontology.impl.OMDocResourceLoaderImpl;

public class OMDocResourceLoaderTest {
	private OMDocResourceLoader omdocResourceLoader;

	@Before
	public void setup() {
		omdocResourceLoader = new OMDocResourceLoaderImpl();
	}

	public OMDocResourceLoader getOmdocResourceLoader() {
		return omdocResourceLoader;
	}

	@Test
	public void testLoadLemmaResource() {
		// TODO : correct file path to make relative it and include files into
		// project
		OntologyResource resource = new OntologyResource(
				"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.omdoc#existlemma");
		OMDocElement omdocElement = getOmdocResourceLoader().load(resource);
		Assert.assertEquals("existlemma", omdocElement.getId());
		SourceReference testSrcRef = new SourceReference();
		testSrcRef
				.setFileName("file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.tex");
		testSrcRef.setLine(93);
		testSrcRef.setColumn(43);
		Assert.assertEquals(testSrcRef, omdocElement.getSrcRef());
		Assert
				.assertEquals(
						"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.omdoc#existlemma",
						omdocElement.getResourceUri());
		Assert
				.assertEquals(
						"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.pdf",
						omdocElement.getPdfFileName());
		Assert
				.assertEquals(
						"Описание конечных нильпотентных групп ступени 2 простого нечетного периода",
						omdocElement.getArticleMetadata().getTitle());
		Assert.assertEquals("А.И. Долгарев", omdocElement.getArticleMetadata()
				.getAuthor());
	}

	@Test
	public void testLoadLemmaProofStepResource() {
		OntologyResource resource = new OntologyResource(
				"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.omdoc#element-existence-lemma-proof.p1");
		OMDocElement omdocElement = getOmdocResourceLoader().load(resource);
		Assert.assertEquals("element-existence-lemma-proof.p1", omdocElement
				.getId());
		SourceReference testSrcRef = new SourceReference();
		testSrcRef
				.setFileName("file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.tex");
		testSrcRef.setLine(101);
		testSrcRef.setColumn(33);
		Assert.assertEquals(testSrcRef, omdocElement.getSrcRef());
		Assert
				.assertEquals(
						"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.omdoc#element-existence-lemma-proof.p1",
						omdocElement.getResourceUri());
		Assert
				.assertEquals(
						"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.pdf",
						omdocElement.getPdfFileName());
	}
}
