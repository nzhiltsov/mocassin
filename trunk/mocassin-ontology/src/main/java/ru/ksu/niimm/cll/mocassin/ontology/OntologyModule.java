package ru.ksu.niimm.cll.mocassin.ontology;

import java.io.IOException;
import java.util.Properties;

import org.openrdf.repository.Repository;

import ru.ksu.niimm.cll.mocassin.ontology.impl.OntologyFacadeImpl;
import ru.ksu.niimm.cll.mocassin.ontology.impl.OntologyResourceFacadeImpl;
import ru.ksu.niimm.cll.mocassin.ontology.impl.QueryManagerFacadeImpl;
import ru.ksu.niimm.cll.mocassin.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.cll.mocassin.ontology.loader.impl.SparqlQueryLoaderImpl;
import ru.ksu.niimm.cll.mocassin.ontology.provider.OntologyProvider;
import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.ontology.provider.impl.InMemoryRepositoryProvider;
import ru.ksu.niimm.cll.mocassin.ontology.provider.impl.OntologyPelletProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.hp.hpl.jena.ontology.OntModel;

public class OntologyModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("ontology_config.properties"));
			Names.bindProperties(binder(), properties);
		} catch (IOException ex) {
			throw new RuntimeException(
					"failed to load the Ontology module configuration");
		}

		bind(OntologyFacade.class).to(OntologyFacadeImpl.class);
		bind(QueryManagerFacade.class).to(QueryManagerFacadeImpl.class);
		bind(OntologyResourceFacade.class).to(OntologyResourceFacadeImpl.class);
		bind(SparqlQueryLoader.class).to(SparqlQueryLoaderImpl.class);
		ThrowingProviderBinder.create(binder())
				.bind(OntologyProvider.class, OntModel.class)
				.to(OntologyPelletProvider.class).in(Singleton.class);
		ThrowingProviderBinder.create(binder())
				.bind(RepositoryProvider.class, Repository.class)
				.to(InMemoryRepositoryProvider.class).in(Singleton.class);
	}

}
