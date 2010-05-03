package ru.ksu.niimm.ose.ontology;

import ru.ksu.niimm.ose.ontology.impl.OMDocOntologyFacadeImpl;
import ru.ksu.niimm.ose.ontology.impl.OMDocResourceFacadeOldImpl;
import ru.ksu.niimm.ose.ontology.impl.OMDocResourceFacadeImpl;
import ru.ksu.niimm.ose.ontology.impl.QueryManagerFacadeImpl;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;
import ru.ksu.niimm.ose.ontology.loader.RDFGraphPropertiesLoader;
import ru.ksu.niimm.ose.ontology.loader.RDFStorageLoader;
import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.ose.ontology.loader.impl.OMDocOntologyLoaderImpl;
import ru.ksu.niimm.ose.ontology.loader.impl.RDFGraphPropertiesLoaderImpl;
import ru.ksu.niimm.ose.ontology.loader.impl.RDFStorageLoaderImpl;
import ru.ksu.niimm.ose.ontology.loader.impl.SparqlQueryLoaderImpl;

import com.google.inject.AbstractModule;

public class OntologyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(OMDocOntologyFacade.class).to(OMDocOntologyFacadeImpl.class);
		bind(QueryManagerFacade.class).to(QueryManagerFacadeImpl.class);
		bind(OMDocResourceFacade.class).to(OMDocResourceFacadeImpl.class);

		bind(RDFStorageLoader.class).to(RDFStorageLoaderImpl.class);
		bind(OMDocOntologyLoader.class).to(OMDocOntologyLoaderImpl.class);
		bind(RDFGraphPropertiesLoader.class).to(
				RDFGraphPropertiesLoaderImpl.class);
		bind(SparqlQueryLoader.class).to(SparqlQueryLoaderImpl.class);
	}

}
