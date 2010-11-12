package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;

import ru.ksu.niimm.ose.ontology.loader.ModulePropertiesLoader;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;

import com.google.inject.Inject;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OMDocOntologyLoaderImpl implements OMDocOntologyLoader {
	private static final String OMDOC_URI_PARAMETER_NAME = "omdoc.uri";
	private ModulePropertiesLoader modulePropertiesLoader;
	private OntModel omdocOntology;

	@Inject
	public OMDocOntologyLoaderImpl(ModulePropertiesLoader modulePropertiesLoader)
			throws IOException {
		this.modulePropertiesLoader = modulePropertiesLoader;
		this.omdocOntology = load();
	}

	@Override
	public OntModel getOntology() {
		return omdocOntology;
	}

	protected OntModel load() throws IOException {
		OntModel model = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		model.read(getOmdocUri());
		return model;
	}

	protected String getOmdocUri() {
		return getModulePropertiesLoader().get(OMDOC_URI_PARAMETER_NAME);
	}

	private ModulePropertiesLoader getModulePropertiesLoader() {
		return modulePropertiesLoader;
	}

}
