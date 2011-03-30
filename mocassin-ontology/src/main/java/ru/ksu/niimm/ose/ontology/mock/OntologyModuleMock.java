package ru.ksu.niimm.ose.ontology.mock;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.ose.ontology.OntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import ru.ksu.niimm.ose.ontology.QueryManagerFacade;
import ru.ksu.niimm.ose.ontology.impl.OntologyFacadeImpl;
import ru.ksu.niimm.ose.ontology.loader.OntologyLoader;
import ru.ksu.niimm.ose.ontology.loader.impl.OntologyPelletLoader;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class OntologyModuleMock extends AbstractModule {

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
		bind(OntologyLoader.class).to(OntologyPelletLoader.class);
		
		bind(QueryManagerFacade.class).to(QueryManagerMock.class);
		bind(OntologyResourceFacade.class).to(OntologyResourceMock.class);
	}

}
