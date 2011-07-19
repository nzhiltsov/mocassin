package ru.ksu.niimm.cll.mocassin.nlp.gate.impl;

import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.persistence.PersistenceManager;

import java.io.File;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AnnieControllerCreationException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AnnieControllerProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AnnieControllerProviderImpl implements
		AnnieControllerProvider<SerialAnalyserController> {
	private final String CONFIG_FILE_NAME;

	@Inject
	public AnnieControllerProviderImpl(
			@Named("gate.loaded.plugin.config") String configName) {
		CONFIG_FILE_NAME = configName;
	}

	@Override
	public SerialAnalyserController get()
			throws AnnieControllerCreationException {
		SerialAnalyserController annie;
		try {
			annie = (SerialAnalyserController) PersistenceManager
					.loadObjectFromFile(new File(new File(
							Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
							CONFIG_FILE_NAME));
		} catch (Exception e) {
			throw new AnnieControllerCreationException(e);
		}
		return annie;
	}
}
