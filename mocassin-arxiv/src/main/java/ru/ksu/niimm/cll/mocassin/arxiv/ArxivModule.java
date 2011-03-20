package ru.ksu.niimm.cll.mocassin.arxiv;

import ru.ksu.niimm.cll.mocassin.arxiv.impl.ArxivDAOFacadeImpl;

import com.google.inject.AbstractModule;

public class ArxivModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ArxivDAOFacade.class).to(ArxivDAOFacadeImpl.class);
	}

}
