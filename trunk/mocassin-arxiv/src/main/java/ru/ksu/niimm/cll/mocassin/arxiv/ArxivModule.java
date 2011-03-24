package ru.ksu.niimm.cll.mocassin.arxiv;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.arxiv.impl.ArxivDAOFacadeImpl;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ArxivModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("arxiv-module.properties"));
			Names.bindProperties(binder(), properties);
		} catch (IOException ex) {
			throw new RuntimeException(
					"failed to load the Arxiv module configuration");
		}
		bind(ArxivDAOFacade.class).to(ArxivDAOFacadeImpl.class);
	}
}
