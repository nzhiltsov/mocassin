package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.AnnotationAnalyzer;
import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceContext;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

import com.google.inject.Inject;

public class FeatureExtractorImpl implements FeatureExtractor {

	private static final String GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY = "gate.document.lr.type";
	private static final String GATE_STORAGE_DIR_PROPERTY_KEY = "gate.storage.dir";
	private static final String GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY = "gate.builtin.creole.dir";
	private static final String GATE_HOME_PROPERTY_KEY = "gate.home";

	private boolean isInitialized = false;
	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;
	@Inject
	private AnnotationAnalyzer annotationAnalyzer;
	@Inject
	private StructuralElementSearcher structuralElementSearcher;

	@Override
	public List<ReferenceContext> getReferenceContextList() throws Exception {
		initialize();
		SerialDataStore dataStore = new SerialDataStore(
				getNlpModulePropertiesLoader().get(
						GATE_STORAGE_DIR_PROPERTY_KEY));
		dataStore.open();
		List documents = dataStore.getLrIds(getDocumentLrType());
		List<ReferenceContext> referenceContextList = new ArrayList<ReferenceContext>();
		for (Object documentLrId : documents) {

			FeatureMap features = Factory.newFeatureMap();
			features.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
			features.put(DataStore.LR_ID_FEATURE_NAME, documentLrId);
			Document document = (Document) Factory.createResource(
					getDocumentLrType(), features);
			List<StructuralElement> structuralElements = getStructuralElementSearcher()
					.retrieve(document);
			throw new UnsupportedOperationException("n.y.i.");
		}
		dataStore.close();
		return referenceContextList;
	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	public AnnotationAnalyzer getAnnotationAnalyzer() {
		return annotationAnalyzer;
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	private String getDocumentLrType() {
		return getNlpModulePropertiesLoader().get(
				GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY);
	}

	private void initialize() throws GateException {
		if (!isInitialized) {
			System.setProperty(GATE_HOME_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(GATE_HOME_PROPERTY_KEY));
			System.setProperty(GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(
							GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY));
			Gate.init();
		}
	}
}