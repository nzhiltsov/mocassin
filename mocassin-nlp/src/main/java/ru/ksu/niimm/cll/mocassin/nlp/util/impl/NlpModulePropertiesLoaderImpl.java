package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

public class NlpModulePropertiesLoaderImpl implements NlpModulePropertiesLoader {
	private static final String PROPERTIES_FILENAME = "nlp_module.properties";
	private Properties properties;

	public NlpModulePropertiesLoaderImpl() throws IOException {
		properties = loadProperties();
	}

	@Override
	public String get(String key) {
		return getProperties().getProperty(key);
	}

	@Override
	public int getWindowTokenSize() {
		String value = getProperties().getProperty("token.window.size");
		return Integer.parseInt(value);
	}

	public Properties getProperties() {
		return properties;
	}

	private Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		ClassLoader loader = NlpModulePropertiesLoaderImpl.class
				.getClassLoader();
		URL url = loader.getResource(PROPERTIES_FILENAME);
		InputStream stream = url.openStream();
		try {
			properties.load(stream);
		} finally {
			stream.close();
		}
		return properties;
	}
}
