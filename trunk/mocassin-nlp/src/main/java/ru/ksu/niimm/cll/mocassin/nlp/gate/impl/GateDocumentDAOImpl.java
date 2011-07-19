package ru.ksu.niimm.cll.mocassin.nlp.gate.impl;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import gate.security.SecurityException;
import gate.util.GateException;
import gate.util.OffsetComparator;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.util.GateDocumentMetadata;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GateDocumentDAOImpl implements GateDocumentDAO {

	private static final String GATE_DOCUMENT_AFFIX = ".tex.xml";
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
	@Inject
	private AnnotationUtil annotationUtil;

	@Override
	public synchronized void save(String documentId, File file)
			throws AccessGateStorageException,
			ru.ksu.niimm.cll.mocassin.nlp.gate.impl.PersistenceException {
		initialize();
		try {
			Document document = Factory.newDocument(file.toURI().toURL());
			Document persistedDocument = (Document) this.dataStore.adopt(
					document, null);
			persistedDocument.setName(StringUtil.arxivid2filename(documentId,
					"tex.xml"));
			this.dataStore.sync(persistedDocument);
		} catch (ResourceInstantiationException e) {
			String message = String.format(
					"failed to create a GATE document for the file='%s'",
					file.getAbsolutePath());
			logger.log(Level.SEVERE, message);
			throw new ru.ksu.niimm.cll.mocassin.nlp.gate.impl.PersistenceException(
					message);
		} catch (MalformedURLException e) {
			String message = String
					.format("failed to create a GATE document for the file='%s': this file path probably is incorrect",
							file.getAbsolutePath());
			logger.log(Level.SEVERE, message);
			throw new IllegalArgumentException(message);
		} catch (PersistenceException e) {
			String message = String.format(
					"failed to save a GATE document for the file='%s'",
					file.getAbsolutePath());
			logger.log(Level.SEVERE, message);
			throw new ru.ksu.niimm.cll.mocassin.nlp.gate.impl.PersistenceException(
					message);
		} catch (SecurityException e) {
			String message = String
					.format("failed to save a GATE document for the file='%s' due to security reasons",
							file.getAbsolutePath());
			logger.log(Level.SEVERE, message);
			throw new ru.ksu.niimm.cll.mocassin.nlp.gate.impl.PersistenceException(
					message);
		}
	}

	@Override
	public synchronized GateDocumentMetadata loadMetadata(String documentId)
			throws AccessGateDocumentException, AccessGateStorageException {
		initialize();
		Document document = load(documentId);
		GateDocumentMetadata metadata = extractMetadata(document);
		release(document);
		return metadata;
	}

	@Override
	public synchronized Document load(String documentId)
			throws AccessGateDocumentException, AccessGateStorageException {
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
	public synchronized List<String> getDocumentIds()
			throws AccessGateDocumentException, AccessGateStorageException {
		initialize();

		try {
			Iterator it = getDataStore().getLrIds(getDocumentLrType())
					.iterator();
			List<String> gateDocuments = new ArrayList<String>();
			while (it.hasNext()) {
				String documentLrId = (String) it.next();
				gateDocuments.add(documentLrId);
			}

			return gateDocuments;
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE,
					"couldn't get language resources identifiers");
			throw new AccessGateDocumentException(e);
		}
	}

	@Override
	public synchronized void release(Document document) {
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

	private void initialize() throws AccessGateStorageException {
		if (!isInitialized) {
			isInitialized = true;
			System.setProperty(GATE_HOME_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(GATE_HOME_PROPERTY_KEY));
			System.setProperty(
					GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY,
					getNlpModulePropertiesLoader().get(
							GATE_BUILTIN_CREOLE_DIR_PROPERTY_KEY));
			try {
				Gate.init();
				this.dataStore = new SerialDataStore(
						getNlpModulePropertiesLoader().get(
								GATE_STORAGE_DIR_PROPERTY_KEY));
				getDataStore().open();
			} catch (GateException e) {
				throw new AccessGateStorageException(e);
			}
		}

	}

	private String getDocumentLrType() {
		return getNlpModulePropertiesLoader().get(
				GATE_DOCUMENT_LR_TYPE_PROPERTY_KEY);
	}

	private GateDocumentMetadata extractMetadata(Document document)
			throws AccessGateDocumentException {
		AnnotationSet allTitleAnnotations = document
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.get(getProperty(GateFormatConstants.TITLE_ANNOTATION_NAME_PROPERTY_KEY));
		String documentName = document.getName().substring(
				0,
				document.getName().lastIndexOf(GATE_DOCUMENT_AFFIX)
						+ GATE_DOCUMENT_AFFIX.length());
		if (allTitleAnnotations.size() == 0) {
			throw new AccessGateDocumentException(String.format(
					"The document %s has the wrong metadata: title is absent",
					documentName));
		}
		AnnotationSet allCreatorAnnotations = document
				.getAnnotations(
						getProperty(GateFormatConstants.ARXMLIV_MARKUP_NAME_PROPERTY_KEY))
				.get(getProperty(GateFormatConstants.ARXMLIV_CREATOR_PROPERTY_KEY));
		if (!allCreatorAnnotations.iterator().hasNext()) {
			throw new AccessGateDocumentException(
					String.format(
							"The document %s has the wrong metadata: no one creator exists",
							documentName));
		}
		List<Annotation> titleList = new ArrayList<Annotation>(
				allTitleAnnotations);
		Collections.sort(titleList, new OffsetComparator());
		Annotation titleAnnotation = titleList.iterator().next();
		String[] strTokens = annotationUtil.getTokenWithMathAnnotation(
				document, titleAnnotation);
		String title = StringUtil.asString(strTokens);

		List<String> authorNames = new LinkedList<String>();

		for (Annotation creator : allCreatorAnnotations) {
			String[] authorTokens = annotationUtil.getPureTokensForAnnotation(
					document, creator, false);
			String authorName = StringUtil.asString(Arrays.copyOfRange(
					authorTokens, 0, 3));
			authorNames.add(authorName);
		}

		return new GateDocumentMetadata(documentName, title, authorNames);
	}

	private String getProperty(String key) {
		return getNlpModulePropertiesLoader().get(key);
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
			return key.equals(input.substring(0,
					input.lastIndexOf(GATE_DOCUMENT_AFFIX)));
		}

	}
}
