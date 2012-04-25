package ru.ksu.niimm.cll.mocassin.crawl.arxiv;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
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
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
