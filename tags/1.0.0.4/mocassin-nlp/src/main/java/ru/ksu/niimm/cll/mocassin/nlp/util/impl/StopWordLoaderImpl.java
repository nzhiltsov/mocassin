package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.util.IOUtils;

public class StopWordLoaderImpl implements StopWordLoader {
	private static final String STOP_LIST_FILENAME = "stop_list.properties";
	private Set<String> stopWords = new HashSet<String>();

	public StopWordLoaderImpl() throws IOException {
		ClassLoader loader = StopWordLoaderImpl.class.getClassLoader();
		URL url = loader.getResource(STOP_LIST_FILENAME);
		this.stopWords = IOUtils.readLineSet(url.openStream());

	}

	@Override
	public Set<String> getStopWords() {
		return this.stopWords;
	}

}
