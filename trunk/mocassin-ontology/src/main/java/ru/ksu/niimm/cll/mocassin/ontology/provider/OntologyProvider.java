package ru.ksu.niimm.cll.mocassin.ontology.provider;

import java.io.IOException;

import com.google.inject.throwingproviders.CheckedProvider;

public interface OntologyProvider<T> extends CheckedProvider<T> {
	T get() throws IOException;
}