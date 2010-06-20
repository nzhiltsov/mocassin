package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.google.inject.Inject;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.ose.ontology.loader.ModulePropertiesLoader;
import ru.ksu.niimm.ose.ontology.loader.RDFGraphPropertiesLoader;

public class RDFGraphPropertiesLoaderImpl implements RDFGraphPropertiesLoader {
	private ModulePropertiesLoader modulePropertiesLoader;
	private RDFGraph graph;

	@Inject
	public RDFGraphPropertiesLoaderImpl(
			ModulePropertiesLoader modulePropertiesLoader) throws IOException {
		this.modulePropertiesLoader = modulePropertiesLoader;
		RDFGraph graph = new RDFGraphImpl.Builder(get("graph.iri")).username(
				get("connection.user.name")).password(
				get("connection.user.password")).url(get("connection.url"))
				.build();
		this.graph = graph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ontology.loader.impl.RDFGraphPropertiesLoader#getGraph()
	 */
	public RDFGraph getGraph() {
		return graph;
	}

	private String get(String key) {
		return getModulePropertiesLoader().get(key);
	}

	private ModulePropertiesLoader getModulePropertiesLoader() {
		return modulePropertiesLoader;
	}

}
