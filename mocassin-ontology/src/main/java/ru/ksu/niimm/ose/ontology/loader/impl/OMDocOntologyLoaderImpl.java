package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.io.InputStream;

import ru.ksu.niimm.ose.ontology.loader.ModulePropertiesLoader;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;

import com.google.inject.Inject;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OMDocOntologyLoaderImpl implements OMDocOntologyLoader {
	private static final String OMDOC_URI_PARAMETER_NAME = "omdoc.uri";
	@Inject
	private ModulePropertiesLoader modulePropertiesLoader;
	private OntModel omdocOntology;

	public OMDocOntologyLoaderImpl() throws IOException {
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

	private String getOmdocUri() {
		return getModulePropertiesLoader().get(OMDOC_URI_PARAMETER_NAME);
	}

	private ModulePropertiesLoader getModulePropertiesLoader() {
		return modulePropertiesLoader;
	}

}
