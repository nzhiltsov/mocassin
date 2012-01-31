package ru.ksu.niimm.cll.mocassin.ontology.provider;

import com.google.inject.throwingproviders.CheckedProvider;

public interface RepositoryProvider<T> extends CheckedProvider<T> {
	T get() throws Exception;
}
