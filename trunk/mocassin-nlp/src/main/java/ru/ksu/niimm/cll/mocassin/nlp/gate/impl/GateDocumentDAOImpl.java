package ru.ksu.niimm.cll.mocassin.nlp.gate.impl;

import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

import com.google.inject.Inject;

public class GateDocumentDAOImpl implements GateDocumentDAO {
	private static final String GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY = "gate.builtin.creole.dir";
	private static final String GATE_HOME_PROPERTY_KEY = "gate.home";

	private static final String GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY = "gate.document.lr.type";
	private static final String GATE_STORAGE_DIR_PROPERTY_KEY = "gate.storage.dir";

	private boolean isInitialized = false;

	private SerialDataStore dataStore;

	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;

	@Override
	public Document load(String documentId) throws GateException {
		initialize();
		getDataStore().open();
		FeatureMap features = Factory.newFeatureMap();
		features.put(DataStore.DATASTORE_FEATURE_NAME, getDataStore());
		features.put(DataStore.LR_ID_FEATURE_NAME, documentId);
		Document document = (Document) Factory.createResource(
				getDocumentLrType(), features);
		return document;
	}

	@Override
	public List<String> getDocumentIds() throws GateException {
		initialize();
		getDataStore().open();
		try {
			List documents = getDataStore().getLrIds(getDocumentLrType());
			List<String> gateDocuments = new ArrayList<String>();
			for (Object documentLrId : documents) {
				gateDocuments.add(documentLrId.toString());
			}
			return gateDocuments;
		} finally {
			getDataStore().close();
		}
	}

	@Override
	public void release(Document document) throws PersistenceException {
		document.cleanup();
		getDataStore().close();
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	private SerialDataStore getDataStore() {
		return dataStore;
	}

	private void initialize() throws GateException {
		if (!isInitialized) {
			System.setProperty(GATE_HOME_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(GATE_HOME_PROPERTY_KEY));
			System.setProperty(GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(
							GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY));
			Gate.init();
			this.dataStore = new SerialDataStore(getNlpModulePropertiesLoader()
					.get(GATE_STORAGE_DIR_PROPERTY_KEY));
		}

	}

	private String getDocumentLrType() {
		return getNlpModulePropertiesLoader().get(
				GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY);
	}
}