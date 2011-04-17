package ru.ksu.niimm.cll.mocassin.ui.server;

import gate.creole.annic.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

import ru.ksu.niimm.cll.mocassin.arxiv.ArxivModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;
import ru.ksu.niimm.ose.ontology.mock.OntologyModuleMock;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class MocassinUIContextListener extends GuiceServletContextListener {
	@Inject
	private Logger logger;
	private Injector injector;

	@Override
	protected Injector getInjector() {
		// this.injector = forMock();
		this.injector = forProduction();
		return this.injector;
	}

	private Injector forProduction() {
		return Guice.createInjector(new MocassinUIModule(),
				new OntologyModule(), new VirtuosoModule(), new ArxivModule(),
				new NlpModule(), new LatexParserModule(), new FullTextModule());
	}

	private Injector forMock() {
		return Guice.createInjector(new OntologyModuleMock(),
				new MocassinUIModule(), new ArxivModule(), new NlpModule(),
				new LatexParserModule(), new FullTextModule());
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		IndexWriter indexWriter = this.injector.getInstance(IndexWriter.class);
		try {
			indexWriter.close();
		} catch (IOException e) {
			logger
					.log(Level.SEVERE,
							"failed to close the index writer. The index might be locked.");
		}
	}

}
