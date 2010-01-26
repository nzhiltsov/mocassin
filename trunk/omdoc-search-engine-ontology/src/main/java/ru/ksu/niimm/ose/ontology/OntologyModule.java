package ru.ksu.niimm.ose.ontology;

import ru.ksu.niimm.ose.ontology.impl.OMDocOntologyFacadeImpl;

import com.google.inject.AbstractModule;

public class OntologyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(OMDocOntologyFacade.class).to(OMDocOntologyFacadeImpl.class);
	}

}
