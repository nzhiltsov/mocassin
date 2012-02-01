package ru.ksu.niimm.cll.mocassin.ontology.provider;

import org.openrdf.repository.RepositoryException;

import com.google.inject.throwingproviders.CheckedProvider;

public interface RepositoryProvider<T> extends CheckedProvider<T> {
	T get() throws RepositoryException;
}
