package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.ui.server.MocassinUIModule;
import ru.ksu.niimm.cll.mocassin.util.IOUtils;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ MocassinUIModule.class, OntologyModule.class,
		VirtuosoModule.class, ArxivModule.class, NlpModule.class,
		LatexParserModule.class, FullTextModule.class, AnalyzerModule.class })
@Ignore("disabled because of interference with the main index")
public class ArXMLivAdapterTest {
	@Inject
	private ArXMLivAdapter arXMLivAdapter;

	private Set<String> ids;

	@Before
	public void init() throws IOException {
		this.ids = IOUtils.readLineSet(this.getClass().getClassLoader()
				.getResourceAsStream("arxiv_ids_list.txt"));
	}

	@Test
	public void testHandle() {
		int number = arXMLivAdapter.handle(ids);
		System.out.println(number
				+ " document(s) have been processed successfully");
	}
}
