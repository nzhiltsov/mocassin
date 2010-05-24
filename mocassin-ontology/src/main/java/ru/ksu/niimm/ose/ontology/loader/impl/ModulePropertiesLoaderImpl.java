package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ru.ksu.niimm.ose.ontology.loader.ModulePropertiesLoader;

public class ModulePropertiesLoaderImpl implements ModulePropertiesLoader {
	private static final String CONFIG_PROPERTIES_FILENAME = "ontology_config.properties";
	private Properties properties;

	public ModulePropertiesLoaderImpl() throws IOException {
		properties = loadProperties();
	}

	@Override
	public String get(String key) {
		return getProperties().getProperty(key);
	}

	private Properties getProperties() {
		return properties;
	}

	private Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		ClassLoader loader = ModulePropertiesLoaderImpl.class
				.getClassLoader();
		InputStream stream = loader.getResourceAsStream(CONFIG_PROPERTIES_FILENAME);
		properties.load(stream);
		stream.close();
		return properties;
	}
}
