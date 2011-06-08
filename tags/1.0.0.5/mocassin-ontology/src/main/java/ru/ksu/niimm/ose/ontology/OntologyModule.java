package ru.ksu.niimm.ose.ontology;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.ose.ontology.impl.OntologyFacadeImpl;
import ru.ksu.niimm.ose.ontology.impl.OntologyResourceFacadeImpl;
import ru.ksu.niimm.ose.ontology.impl.QueryManagerFacadeImpl;
import ru.ksu.niimm.ose.ontology.loader.OntologyLoader;
import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.ose.ontology.loader.impl.OntologyPelletLoader;
import ru.ksu.niimm.ose.ontology.loader.impl.SparqlQueryLoaderImpl;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

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
		bind(OntologyLoader.class).to(OntologyPelletLoader.class);
		bind(SparqlQueryLoader.class).to(SparqlQueryLoaderImpl.class);
		
	}

}
