package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.util.List;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OMDocOntologyPelletLoader extends OMDocOntologyLoaderImpl
		implements OMDocOntologyLoader {

	public OMDocOntologyPelletLoader() throws IOException {
		super();
	}

	@Override
	protected OntModel load() throws IOException {
		OntModel model = ModelFactory
				.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read("http://omdoc.org/ontology#");
		/*
		 * OntProperty exemplifiesProperty = baseModel
		 * .getOntProperty("http://omdoc.org/ontology#exemplifies");
		 * ExtendedIterator<? extends OntResource> it = exemplifiesProperty
		 * .listRange(); while (it.hasNext()) { OntResource resource =
		 * it.next(); if (resource.canAs(OntClass.class)) { OntClass ontClass =
		 * resource.asClass(); ontClass.listSubClasses().toList(); } }
		 */
		return model;
	}
}
