package ru.ksu.niimm.cll.mocassin.nlp.gate.impl;

import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

import com.google.inject.Inject;

public class GateDocumentDAOImpl implements GateDocumentDAO {

	private static final String GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY = "gate.builtin.creole.dir";
	private static final String GATE_HOME_PROPERTY_KEY = "gate.home";

	private static final String GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY = "gate.document.lr.type";
	private static final String GATE_STORAGE_DIR_PROPERTY_KEY = "gate.storage.dir";

	@Inject
	private Logger logger;

	private boolean isInitialized = false;

	private SerialDataStore dataStore;

	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;

	@Override
	public Document load(String documentId) throws GateException {
		initialize();
		FeatureMap features = Factory.newFeatureMap();
		features.put(DataStore.DATASTORE_FEATURE_NAME, getDataStore());
		features.put(DataStore.LR_ID_FEATURE_NAME, documentId);
		Document document;
		try {
			document = (Document) Factory.createResource(getDocumentLrType(),
					features);
		} catch (ResourceInstantiationException e) {
			logger.log(Level.SEVERE, String.format(
					"couldn't create resource with id='%s' caused by: %s", documentId, e.getMessage()));
			getDataStore().close();
			throw new GateException(e);
		}
		return document;
	}

	@Override
	public List<String> getDocumentIds() throws GateException {
		initialize();
		
		try {
			List documents = getDataStore().getLrIds(getDocumentLrType());
			List<String> gateDocuments = new ArrayList<String>();
			for (Object documentLrId : documents) {
				gateDocuments.add(documentLrId.toString());
			}
			Collections.sort(gateDocuments);
			return gateDocuments;
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE,
					"couldn't get language resources identifiers");
			getDataStore().close();
			throw new GateException(e);
		}	
	}

	@Override
	public void release(Document document) throws PersistenceException {
		Factory.deleteResource(document);
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	private SerialDataStore getDataStore() {
		return dataStore;
	}

	private void initialize() throws GateException {
		if (!isInitialized) {
			isInitialized = true;
			System.setProperty(GATE_HOME_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(GATE_HOME_PROPERTY_KEY));
			System.setProperty(GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(
							GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY));
			Gate.init();
			this.dataStore = new SerialDataStore(getNlpModulePropertiesLoader()
					.get(GATE_STORAGE_DIR_PROPERTY_KEY));
			getDataStore().open();
		}

	}

	private String getDocumentLrType() {
		return getNlpModulePropertiesLoader().get(
				GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY);
	}
}
