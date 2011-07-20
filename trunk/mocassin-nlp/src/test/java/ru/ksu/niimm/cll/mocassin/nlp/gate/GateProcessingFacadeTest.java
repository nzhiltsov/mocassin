package ru.ksu.niimm.cll.mocassin.nlp.gate;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, OntologyModule.class, VirtuosoModule.class,
		LatexParserModule.class, FullTextModule.class })
public class GateProcessingFacadeTest {
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	@Test
	public void testProcess() throws AccessGateDocumentException, AccessGateStorageException, ProcessException {
		gateProcessingFacade.process("math/0002188");
		gateProcessingFacade.process("math/0001036");
	}
}
