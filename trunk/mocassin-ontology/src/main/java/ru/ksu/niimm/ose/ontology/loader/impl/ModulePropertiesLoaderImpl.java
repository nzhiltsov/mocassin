package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ru.ksu.niimm.ose.ontology.loader.ModulePropertiesLoader;

public class ModulePropertiesLoaderImpl implements ModulePropertiesLoader {
	private static final String CONFIG_PROPERTIES_FILENAME = "ontology_config.properties";
	protected Properties properties;

	public ModulePropertiesLoaderImpl() throws IOException {
		properties = loadProperties();
	}

	@Override
	public String get(String key) {
		return getProperties().getProperty(key);
	}

	protected Properties getProperties() {
		return properties;
	}

	protected Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		ClassLoader loader = ModulePropertiesLoaderImpl.class.getClassLoader();
		InputStream stream = loader
				.getResourceAsStream(getConfigPropertiesFilename());
		properties.load(stream);
		stream.close();
		return properties;
	}

	protected String getConfigPropertiesFilename() {
		return CONFIG_PROPERTIES_FILENAME;
	}
}
