package ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.impl;

import java.io.IOException;
import java.io.InputStream;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.OntologyProvider;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OntologyPelletProvider implements OntologyProvider<OntModel> {
	private static final String ONTOLOGY_LOADING_MODE_PARAMETER_NAME = "ontology.loading.mode";

	@InjectLogger
	private Logger logger;

	private final String ontologyLoadingMode;

	private final String ontologyUri;

	private final String ontologyLocalFilename;

	@Inject
	public OntologyPelletProvider(
			@Named(ONTOLOGY_LOADING_MODE_PARAMETER_NAME) String ontologyLoadingMode,
			@Named("ontology.uri") String ontologyUri,
			@Named("ontology.local.filename") String ontologyLocalFilename) {
		this.ontologyLoadingMode = ontologyLoadingMode;
		this.ontologyUri = ontologyUri;
		this.ontologyLocalFilename = ontologyLocalFilename;
	}

	@Override
	public OntModel get() throws IOException {
		OntModel model = ModelFactory
				.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		return readOntology(model);
	}

	private OntModel readOntology(OntModel model) {
		OntologyLoadingMode mode = OntologyLoadingMode
				.getModeFromString(this.ontologyLoadingMode);
		if (mode == null) {
			throw new IllegalArgumentException(
					String.format(
							"The ontology loading mode is null. Please check the '%s' parameter in the configuration. ",
							ONTOLOGY_LOADING_MODE_PARAMETER_NAME));
		}
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
		ClassLoader classLoader = OntologyPelletProvider.class.getClassLoader();
		try {
			InputStream stream = classLoader
					.getResourceAsStream(this.ontologyLocalFilename);
			model.read(stream, null);
			stream.close();
		} catch (IOException e) {
			logger.error("Couldn't model as local file with name: {}",
					this.ontologyLocalFilename, e);
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
			if (modeStr == null) {
				return null;
			}
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
