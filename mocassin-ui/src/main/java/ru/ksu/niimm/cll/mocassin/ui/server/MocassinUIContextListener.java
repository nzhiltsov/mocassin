package ru.ksu.niimm.cll.mocassin.ui.server;

import ru.ksu.niimm.cll.mocassin.arxiv.ArxivModule;
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
//		return forMock();
	}

	private Injector forProduction() {
		return Guice.createInjector(new MocassinUIModule(),
				new OntologyModule(), new VirtuosoModule(), new ArxivModule());
	}

	private Injector forMock() {
		return Guice.createInjector(new OntologyModuleMock(), new MocassinUIModule(), new ArxivModule());
	}
}
