package unittest.util;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.ontology.impl.OntologyFacadeImpl;
import ru.ksu.niimm.cll.mocassin.ontology.impl.OntologyResourceFacadeImpl;
import ru.ksu.niimm.cll.mocassin.ontology.impl.QueryManagerFacadeImpl;
import ru.ksu.niimm.cll.mocassin.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.cll.mocassin.ontology.loader.impl.SparqlQueryLoaderImpl;
import ru.ksu.niimm.cll.mocassin.ontology.provider.OntologyProvider;
import ru.ksu.niimm.cll.mocassin.ontology.provider.impl.OntologyPelletProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.hp.hpl.jena.ontology.OntModel;

public class OntologyTestModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("test_ontology_config.properties"));
			Names.bindProperties(binder(), properties);
		} catch (IOException ex) {
			throw new RuntimeException(
					"failed to load the Virtuoso module configuration");
		}

		bind(OntologyFacade.class).to(OntologyFacadeImpl.class);
		bind(QueryManagerFacade.class).to(QueryManagerFacadeImpl.class);
		bind(OntologyResourceFacade.class).to(OntologyResourceFacadeImpl.class);
		bind(SparqlQueryLoader.class).to(SparqlQueryLoaderImpl.class);
		ThrowingProviderBinder.create(binder())
		.bind(OntologyProvider.class, OntModel.class)
		.to(OntologyPelletProvider.class).in(Singleton.class);

	}

}
