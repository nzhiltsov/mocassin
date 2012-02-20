package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.PersistenceException;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTestModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.ui.server.MocassinUIModule;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.IOUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ MocassinUIModule.class, OntologyTestModule.class,
		VirtuosoModule.class, ArxivModule.class, NlpModule.class,
		LatexParserModule.class, PdfParserModule.class, FullTextModule.class,
		AnalyzerModule.class, GateModule.class })
public class ArXMLivAdapterTest {
	@Inject
	private ArXMLivAdapter arXMLivAdapter;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	private List<String> sample;

	@Before
	public void init() throws IOException, AccessGateDocumentException,
			PersistenceException {
		Set<String> ids = IOUtil.readLineSet(this.getClass().getClassLoader()
				.getResourceAsStream("mathnet_ids_list.txt"));
		this.sample = CollectionUtil.sampleRandomSublist(
				new ArrayList<String>(ids), 3);
		for (String id : this.sample) {
			this.gateDocumentDAO.delete(id);
		}
	}

	@Test
	public void testHandle() {
		int number = arXMLivAdapter.handle(sample);
		Assert.assertEquals(
				"Not all the documents have been processed successfully.",
				this.sample.size(), number);
	}
}
