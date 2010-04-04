package ru.ksu.niimm.cll.mocassin.virtuoso;

import ru.ksu.niimm.cll.mocassin.virtuoso.impl.VirtuosoDAOImpl;

import com.google.inject.AbstractModule;

public class VirtuosoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(VirtuosoDAO.class).to(VirtuosoDAOImpl.class);
	}

}
