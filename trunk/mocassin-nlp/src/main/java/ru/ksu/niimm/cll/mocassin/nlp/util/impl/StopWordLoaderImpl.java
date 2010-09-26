package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;

public class StopWordLoaderImpl implements StopWordLoader {
	private static final String STOP_LIST_FILENAME = "stop_list.properties";
	private Set<String> stopWords = new HashSet<String>();

	public StopWordLoaderImpl() throws IOException {
		ClassLoader loader = StopWordLoaderImpl.class.getClassLoader();
		URL url = loader.getResource(STOP_LIST_FILENAME);
		InputStream stream = url.openStream();
		try {
			LineNumberReader lineReader = new LineNumberReader(
					new InputStreamReader(stream));
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				this.stopWords.add(line);
			}

		} finally {
			stream.close();
		}

	}

	@Override
	public Set<String> getStopWords() {
		return this.stopWords;
	}

}
