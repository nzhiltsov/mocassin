package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.FeatureExtractor;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

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
	public void processReferences(int count) throws Exception {

		List<String> documentIds = getGateDocumentDAO().getDocumentIds();
		List<String> selectedDocumentIds = count == 0 ? documentIds
				: CollectionUtil.sampleRandomSublist(documentIds, count);
		for (String id : selectedDocumentIds) {
			ParsedDocument document = new ParsedDocumentImpl(id);
			List<Reference> references = getReferenceSearcher().retrieveReferences(
					document);
			fireReferenceFinishEvent(document, references);

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

	private void fireReferenceFinishEvent(ParsedDocument document,
			List<Reference> references) {
		for (ReferenceProcessListener listener : getListeners()) {
			listener.onReferenceFinish(document, references);
		}
	}

}
