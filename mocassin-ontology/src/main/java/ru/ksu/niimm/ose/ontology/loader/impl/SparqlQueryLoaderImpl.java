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
import java.util.Map;

import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;

public class SparqlQueryLoaderImpl implements SparqlQueryLoader {
	private Map<String, String> name2Query = new HashMap<String, String>();

	public SparqlQueryLoaderImpl() throws IOException {

		String value = readContents("sparql/GetAuthors.sparql");
		getName2Query().put("GetAuthors", value);
		String value2 = readContents("sparql/GetTitle.sparql");
		getName2Query().put("GetTitle", value2);
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

}
