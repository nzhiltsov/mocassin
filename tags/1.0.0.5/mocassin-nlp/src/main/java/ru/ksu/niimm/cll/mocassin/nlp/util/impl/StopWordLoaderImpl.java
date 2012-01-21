package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.util.IOUtils;

import com.google.common.base.Predicate;

public class StopWordLoaderImpl implements StopWordLoader {
	private static final String STOP_LIST_FILENAME = "stop_list.properties";
	private final Set<String> stopWords;
	private final NonStopWordPredicate nonStopWordPredicate = new NonStopWordPredicate();

	public StopWordLoaderImpl() throws IOException {
		ClassLoader loader = StopWordLoaderImpl.class.getClassLoader();
		URL url = loader.getResource(STOP_LIST_FILENAME);
		this.stopWords = IOUtils.readLineSet(url.openStream());
	}

	@Override
	public Set<String> getStopWords() {
		return this.stopWords;
	}

	public NonStopWordPredicate getNonStopWordPredicate() {
		return nonStopWordPredicate;
	}

	public class NonStopWordPredicate implements Predicate<String> {

		@Override
		public boolean apply(String input) {
			return !getStopWords().contains(input.toLowerCase());
		}

	}
}