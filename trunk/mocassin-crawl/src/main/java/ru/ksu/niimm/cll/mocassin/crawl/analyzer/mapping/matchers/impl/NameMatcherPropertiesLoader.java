package ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping.matchers.impl;

import java.util.List;

public interface NameMatcherPropertiesLoader {
	String get(String key);

	List<String> getMatchedURIs();
}
