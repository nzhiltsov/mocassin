package ru.ksu.niimm.cll.mocassin.ui.server;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class MocassinUIContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new MocassinUIModule(),
				new OntologyModule());
	}

}
