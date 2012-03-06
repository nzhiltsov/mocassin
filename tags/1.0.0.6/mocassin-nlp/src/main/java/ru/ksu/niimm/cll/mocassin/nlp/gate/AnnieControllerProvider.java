package ru.ksu.niimm.cll.mocassin.nlp.gate;

import com.google.inject.throwingproviders.CheckedProvider;

public interface AnnieControllerProvider<T> extends CheckedProvider<T> {
	T get() throws AnnieControllerCreationException;
}
