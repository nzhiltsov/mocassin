package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Factory;
import gate.Gate;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.nlp.Feature;
import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;

public class FeatureExtractorImpl implements FeatureExtractor {

	private static final String STORAGE_DIR = "/WINDOWS/gate-serial-datastore";

	@Override
	public Map<String, List<Feature>> getReference2FeatureMap()
			throws Exception {
		System.setProperty("gate.home", "/WINDOWS/programs/gate-5.2.1-build3581-BIN");
		Gate.init();
		SerialDataStore dataStore = new SerialDataStore(STORAGE_DIR);
		dataStore.open();
		dataStore.getLrTypes();
		throw new UnsupportedOperationException();
	}

}
