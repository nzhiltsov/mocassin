package ru.ksu.niimm.cll.mocassin.rdf.ontology;

import java.io.IOException;
import java.util.Properties;

import org.openrdf.repository.Repository;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.impl.OntologyFacadeImpl;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.impl.OntologyResourceFacadeImpl;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.impl.QueryManagerFacadeImpl;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.loader.impl.SparqlQueryLoaderImpl;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.OntologyProvider;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.impl.OntologyPelletProvider;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.impl.VirtuosoRepositoryProvider;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.query.DescribeQueryGenerator;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.query.DescribeQueryGeneratorImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
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
		bind(DescribeQueryGenerator.class).to(DescribeQueryGeneratorImpl.class);
		ThrowingProviderBinder.create(binder())
				.bind(OntologyProvider.class, OntModel.class)
				.to(OntologyPelletProvider.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
		configureStore();
	}

	protected void configureStore() {
		ThrowingProviderBinder.create(binder())
				.bind(RepositoryProvider.class, Repository.class)
				.to(VirtuosoRepositoryProvider.class);
	}

}
