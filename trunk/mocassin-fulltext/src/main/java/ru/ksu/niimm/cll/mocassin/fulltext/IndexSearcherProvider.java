package ru.ksu.niimm.cll.mocassin.fulltext;

import java.io.IOException;

import com.google.inject.throwingproviders.CheckedProvider;

public interface IndexSearcherProvider<T> extends CheckedProvider<T> {
	T get() throws IOException;
}
