package ru.ksu.niimm.cll.mocassin.nlp.impl;

import gate.Document;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;

import com.google.inject.Inject;

public class FeatureExtractorImpl implements FeatureExtractor {

	@Inject
	private NlpModulePropertiesLoader nlpModulePropertiesLoader;
	@Inject
	private ReferenceSearcher referenceSearcher;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	private List<ReferenceProcessListener> listeners = new ArrayList<ReferenceProcessListener>();

	@Override
	public void addListener(ReferenceProcessListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void processReferences() throws Exception {
		List<String> documentIds = getGateDocumentDAO().getDocumentIds();
		for (String id : documentIds) {
			Document document = getGateDocumentDAO().load(id);
			List<Reference> references = getReferenceSearcher().retrieve(
					document);
			fireReferenceFinishEvent(document, references);
			getGateDocumentDAO().release(document);
		}

	}

	public NlpModulePropertiesLoader getNlpModulePropertiesLoader() {
		return nlpModulePropertiesLoader;
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

	public ReferenceSearcher getReferenceSearcher() {
		return referenceSearcher;
	}

	public List<ReferenceProcessListener> getListeners() {
		return listeners;
	}

	private void fireReferenceFinishEvent(Document document,
			List<Reference> references) {
		for (ReferenceProcessListener listener : getListeners()) {
			listener.onReferenceFinish(document, references);
		}
	}

}
