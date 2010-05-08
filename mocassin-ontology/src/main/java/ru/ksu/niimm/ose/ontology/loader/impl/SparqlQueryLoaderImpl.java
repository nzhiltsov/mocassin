package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ibm.icu.util.StringTokenizer;

import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;
import unittest.util.LoadPropertiesUtil;

public class SparqlQueryLoaderImpl implements SparqlQueryLoader {
	private static final String NAMES_PARAMETER = "scriptNames";
	private static final String PROPERTIES_FILENAME = "sparql/scripts.properties";
	private Properties properties = new Properties();
	private Map<String, String> name2Query = new HashMap<String, String>();

	public SparqlQueryLoaderImpl() throws IOException {
		ClassLoader loader = SparqlQueryLoaderImpl.class.getClassLoader();
		URL url = loader.getResource(PROPERTIES_FILENAME);
		InputStream stream = url.openStream();
		properties.load(stream);
		stream.close();

		String names = getProperties().getProperty(NAMES_PARAMETER);

		StringTokenizer st = new StringTokenizer(names, ",");

		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			String value = readContents(String.format("sparql/%s.sparql", name));
			getName2Query().put(name, value);
		}
	}

	private String readContents(String path) throws IOException {
		ClassLoader loader = SparqlQueryLoaderImpl.class.getClassLoader();
		URL url = loader.getResource(path);
		InputStream inputStream = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuffer contentsBuffer = new StringBuffer();
		String value;
		while ((value = reader.readLine()) != null) {
			contentsBuffer.append(value);
		}
		reader.close();
		inputStream.close();
		return contentsBuffer.toString();

	}

	@Override
	public String loadQueryByName(String queryName) {
		if (!getName2Query().containsKey(queryName)) {
			throw new IllegalArgumentException(String.format(
					"cannot load sparql with key: %s", queryName));
		}
		return getName2Query().get(queryName);
	}

	public Map<String, String> getName2Query() {
		return name2Query;
	}

	public Properties getProperties() {
		return properties;
	}

}
