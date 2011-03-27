package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;

import ru.ksu.niimm.ose.ontology.loader.OntologyLoader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OntologyLoaderImpl implements OntologyLoader {
	private String ontologyUri;
	private OntModel omdocOntology;

	@Inject
	public OntologyLoaderImpl(@Named("ontology.uri") String ontologyUri)
			throws IOException {
		this.ontologyUri = ontologyUri;
		this.omdocOntology = load();
	}

	@Override
	public OntModel getOntology() {
		return omdocOntology;
	}

	protected OntModel load() throws IOException {
		OntModel model = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		model.read(getOntologyUri());
		return model;
	}

	protected String getOntologyUri() {
		return this.ontologyUri;
	}

}
