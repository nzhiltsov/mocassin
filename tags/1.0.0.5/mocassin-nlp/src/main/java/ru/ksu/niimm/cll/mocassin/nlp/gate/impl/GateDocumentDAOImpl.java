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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
	public Document load(String documentId) throws AccessGateDocumentException {
		initialize();
		List<String> documentIds = getDocumentIds();
		String foundDocumentId = Iterables.find(documentIds,
				new DocumentNamePredicate(documentId), null);
		if (foundDocumentId == null)
			throw new IllegalArgumentException(String.format(
					"document with a key='%s' not found", documentId));
		FeatureMap features = Factory.newFeatureMap();
		features.put(DataStore.DATASTORE_FEATURE_NAME, getDataStore());
		features.put(DataStore.LR_ID_FEATURE_NAME, foundDocumentId);
		Document document;
		try {
			document = (Document) Factory.createResource(getDocumentLrType(),
					features);
		} catch (ResourceInstantiationException e) {
			logger.log(Level.SEVERE, String.format(
					"couldn't create resource with id='%s' caused by: %s",
					documentId, e.getMessage()));
			throw new AccessGateDocumentException(e);
		}
		return document;
	}

	@Override
	public List<String> getDocumentIds() throws AccessGateDocumentException {
		initialize();

		try {
			Iterator it = getDataStore().getLrIds(getDocumentLrType())
					.iterator();
			List<String> gateDocuments = new ArrayList<String>();
			while (it.hasNext()) {
				String documentLrId = (String) it.next();
				gateDocuments.add(documentLrId);
			}
			Collections.sort(gateDocuments);
			return gateDocuments;
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE,
					"couldn't get language resources identifiers");
			throw new AccessGateDocumentException(e);
		}
	}

	@Override
	public void release(Document document) {
		if (document != null) {
			Factory.deleteResource(document);
		}
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	private SerialDataStore getDataStore() {
		return dataStore;
	}

	private void initialize() throws AccessGateDocumentException {
		if (!isInitialized) {
			isInitialized = true;
			System.setProperty(GATE_HOME_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(GATE_HOME_PROPERTY_KEY));
			System.setProperty(GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(
							GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY));
			try {
				Gate.init();
				this.dataStore = new SerialDataStore(
						getNlpModulePropertiesLoader().get(
								GATE_STORAGE_DIR_PROPERTY_KEY));
				getDataStore().open();
			} catch (GateException e) {
				throw new AccessGateDocumentException(e);
			}
		}

	}

	private String getDocumentLrType() {
		return getNlpModulePropertiesLoader().get(
				GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY);
	}

	private static class DocumentNamePredicate implements Predicate<String> {
		private final String key;

		private DocumentNamePredicate(String key) {
			this.key = key;
		}

		@Override
		public boolean apply(String input) {
			if (input == null)
				return false;
			return input.startsWith(key);
		}

	}
}