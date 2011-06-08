package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import ru.ksu.niimm.ose.ontology.loader.OntologyLoader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OntologyPelletLoader implements OntologyLoader {
	@Inject
	private Logger logger;

	private OntModel omdocOntology;

	private final String ontologyLoadingMode;

	private final String ontologyUri;

	private final String ontologyLocalFilename;

	@Inject
	public OntologyPelletLoader(
			@Named("ontology.loading.mode") String ontologyLoadingMode,
			@Named("ontology.uri") String ontologyUri,
			@Named("ontology.local.filename") String ontologyLocalFilename)
			throws IOException {
		this.ontologyLoadingMode = ontologyLoadingMode;
		this.ontologyUri = ontologyUri;
		this.ontologyLocalFilename = ontologyLocalFilename;
		this.omdocOntology = load();
	}

	@Override
	public OntModel getOntology() {
		return omdocOntology;
	}

	protected OntModel load() throws IOException {
		OntModel model = ModelFactory
				.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		return readOntology(model);
	}

	private OntModel readOntology(OntModel model) {
		OntologyLoadingMode mode = OntologyLoadingMode.getModeFromString(this.ontologyLoadingMode);
		switch (mode) {
		case REMOTE:
			return loadAsRemote(model);
		case LOCAL:
			return loadAsLocal(model);
		default:
			throw new UnsupportedOperationException(String.format(
					"this mode is unsupported: %s", mode));
		}

	}

	private OntModel loadAsLocal(OntModel model) {
		ClassLoader classLoader = OntologyPelletLoader.class.getClassLoader();
		try {
			InputStream stream = classLoader
					.getResourceAsStream(this.ontologyLocalFilename);
			model.read(stream, null);
			stream.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "couldn't model as local file with name:"
					+ this.ontologyLocalFilename);
		}
		return model;
	}

	private OntModel loadAsRemote(OntModel model) {
		model.read(this.ontologyUri);
		return model;
	}

	enum OntologyLoadingMode {

		REMOTE, LOCAL;

		public static OntologyLoadingMode getModeFromString(String modeStr) {
			if (modeStr == null)
				return null;
			for (OntologyLoadingMode mode : values()) {
				if (mode.toString().toLowerCase().equals(modeStr)) {
					return mode;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

	}
}
