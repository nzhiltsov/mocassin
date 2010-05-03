package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;

public class SparqlQueryLoaderImpl implements SparqlQueryLoader {
	private static final String SPARQL_SCRIPTS_DIR_PATH = "sparql";
	private Map<String, String> name2Query = new HashMap<String, String>();

	public SparqlQueryLoaderImpl() throws IOException {
		ClassLoader loader = SparqlQueryLoaderImpl.class.getClassLoader();
		URL url = loader.getResource(SPARQL_SCRIPTS_DIR_PATH);
		File file = new File(url.getPath());
		File[] files = file.listFiles();
		for (File f : files) {
			String value = readContents(f);
			String name = f.getName();
			String pureName = name.substring(0, name.indexOf("."));
			getName2Query().put(pureName, value);
		}
	}

	private String readContents(File f) throws IOException {

		InputStream inputStream = new FileInputStream(f);
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

	public static void main(String[] args) throws IOException {
		SparqlQueryLoaderImpl l = new SparqlQueryLoaderImpl();
	}
}
