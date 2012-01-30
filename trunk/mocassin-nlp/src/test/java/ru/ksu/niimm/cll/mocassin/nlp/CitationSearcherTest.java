package ru.ksu.niimm.cll.mocassin.nlp;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, GateModule.class, LatexParserModule.class,
		PdfParserModule.class })
public class CitationSearcherTest {
	@Inject
	private CitationSearcher citationSearcher;

	@Test
	public void testGetCitationSentences() throws IOException {
		LinkedList<Citation> citations = citationSearcher
				.getCitations("ivm537");
		Assert.assertTrue(citations.size() > 0);
	}
}
