package ru.ksu.niimm.cll.mocassin.nlp;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.gate.ProcessException;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, GateModule.class, LatexParserModule.class,
		PdfParserModule.class })
public class CitationSearcherTest {
	private static final String DOC_ID = "ivm18";
	@Inject
	private CitationSearcher citationSearcher;
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	@Before
	public void init() throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException {
		gateProcessingFacade.process(DOC_ID);
	}

	@Test
	public void testGetCitations() throws IOException {
		LinkedList<Citation> citations = citationSearcher.getCitations(DOC_ID);
		Assert.assertEquals(
				"Number of extracted citations does not equal to the expected one.",
				8, citations.size());
	}
}
