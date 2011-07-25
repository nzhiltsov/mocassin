package ru.ksu.niimm.cll.mocassin.ui.server;

import ru.ksu.niimm.cll.mocassin.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;
import ru.ksu.niimm.ose.ontology.mock.OntologyModuleMock;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class MocassinUIContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return forProduction();
	}

	private Injector forProduction() {
		return Guice.createInjector(new MocassinUIModule(),
				new OntologyModule(), new VirtuosoModule(), new ArxivModule(),
				new NlpModule(), new LatexParserModule(), new FullTextModule(), new AnalyzerModule(), new PdfParserModule());
	}

	private Injector forMock() {
		return Guice.createInjector(new OntologyModuleMock(),
				new MocassinUIModule(), new ArxivModule(), new NlpModule(),
				new LatexParserModule(), new FullTextModule(), new AnalyzerModule(), new PdfParserModule());
	}

}
