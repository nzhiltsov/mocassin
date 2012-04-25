package ru.ksu.niimm.cll.mocassin.rdf.ontology.provider;

import org.openrdf.repository.RepositoryException;

import com.google.inject.throwingproviders.CheckedProvider;

public interface RepositoryProvider<T> extends CheckedProvider<T> {
	T get() throws RepositoryException;
}
