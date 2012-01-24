package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyModule;
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
@GuiceContext({ MocassinUIModule.class, OntologyModule.class,
		VirtuosoModule.class, ArxivModule.class, NlpModule.class,
		LatexParserModule.class, PdfParserModule.class, FullTextModule.class,
		AnalyzerModule.class, GateModule.class })
@Ignore("Ignore until other test will be passed")
public class ArXMLivAdapterTest {
	@Inject
	private ArXMLivAdapter arXMLivAdapter;

	private Set<String> ids;

	@Before
	public void init() throws IOException {
		this.ids = IOUtil.readLineSet(this.getClass().getClassLoader()
				.getResourceAsStream("mathnet_ids_list.txt"));
	}

	@Test
	public void testHandle() {
		List sample = CollectionUtil
				.sampleRandomSublist(new ArrayList(ids), 2);
		int number = arXMLivAdapter.handle(new HashSet(sample));
		System.out.println(number
				+ " document(s) have been processed successfully");
	}
}
