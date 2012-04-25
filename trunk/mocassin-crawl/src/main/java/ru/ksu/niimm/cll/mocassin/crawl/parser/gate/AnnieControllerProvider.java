package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import com.google.inject.throwingproviders.CheckedProvider;

public interface AnnieControllerProvider<T> extends CheckedProvider<T> {
	T get() throws AnnieControllerCreationException;
}
