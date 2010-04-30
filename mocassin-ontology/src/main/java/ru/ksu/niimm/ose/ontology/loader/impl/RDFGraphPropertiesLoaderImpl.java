package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.ose.ontology.loader.RDFGraphPropertiesLoader;
import unittest.util.LoadPropertiesUtil;

public class RDFGraphPropertiesLoaderImpl implements RDFGraphPropertiesLoader {
	private static final String CONFIG_PROPERTIES_FILENAME = "virtuoso-connect.properties";
	private RDFGraph graph;

	public RDFGraphPropertiesLoaderImpl() throws IOException {
		Properties properties = loadProperties();
		RDFGraph graph = new RDFGraphImpl.Builder(properties
				.getProperty("graph.iri")).username(
				properties.getProperty("connection.user.name")).password(
				properties.getProperty("connection.user.password")).url(
				properties.getProperty("connection.url")).build();
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

	private Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		ClassLoader loader = RDFGraphPropertiesLoaderImpl.class
				.getClassLoader();
		URL url = loader.getResource(CONFIG_PROPERTIES_FILENAME);
		properties.load(url.openStream());
		return properties;
	}

}
