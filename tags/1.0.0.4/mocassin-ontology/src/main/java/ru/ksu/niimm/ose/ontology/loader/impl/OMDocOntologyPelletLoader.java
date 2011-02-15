package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.io.InputStream;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import ru.ksu.niimm.ose.ontology.loader.ModulePropertiesLoader;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;

import com.google.inject.Inject;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OMDocOntologyPelletLoader implements OMDocOntologyLoader {
	private static final String OMDOC_LOCAL_FILENAME_PARAMETER_NAME = "omdoc.local.filename";
	private static final String OMDOC_LOADING_MODE_PARAMETER_NAME = "omdoc.loading.mode";
	private static final String OMDOC_URI_PARAMETER_NAME = "omdoc.uri";
	private ModulePropertiesLoader modulePropertiesLoader;
	private OntModel omdocOntology;

	@Inject
	public OMDocOntologyPelletLoader(
			ModulePropertiesLoader modulePropertiesLoader) throws IOException {
		this.modulePropertiesLoader = modulePropertiesLoader;
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
		String omdocLoadingMode = getModulePropertiesLoader().get(
				OMDOC_LOADING_MODE_PARAMETER_NAME);
		OMDocMode mode = OMDocMode.getModeFromString(omdocLoadingMode);
		switch (mode) {
		case REMOTE:
			String omdocUri = getModulePropertiesLoader().get(
					OMDOC_URI_PARAMETER_NAME);
			return loadAsRemote(model, omdocUri);
		case LOCAL:
			String omdocFilename = getModulePropertiesLoader().get(
					OMDOC_LOCAL_FILENAME_PARAMETER_NAME);
			return loadAsLocal(model, omdocFilename);
		default:
			throw new UnsupportedOperationException(String.format(
					"this mode is unsupported: %s", mode));
		}

	}

	private OntModel loadAsLocal(OntModel model, String fileName) {
		ClassLoader classLoader = OMDocOntologyPelletLoader.class
				.getClassLoader();
		try {
			InputStream stream = classLoader.getResourceAsStream(fileName);
			model.read(stream, null);
			stream.close();
		} catch (IOException e) {
			// TODO: add log message!!
		}
		return model;
	}

	private OntModel loadAsRemote(OntModel model, String uri) {
		model.read(uri);
		return model;
	}

	private ModulePropertiesLoader getModulePropertiesLoader() {
		return modulePropertiesLoader;
	}

	enum OMDocMode {

		REMOTE, LOCAL;

		public static OMDocMode getModeFromString(String modeStr) {
			if (modeStr == null)
				return null;
			for (OMDocMode mode : values()) {
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
