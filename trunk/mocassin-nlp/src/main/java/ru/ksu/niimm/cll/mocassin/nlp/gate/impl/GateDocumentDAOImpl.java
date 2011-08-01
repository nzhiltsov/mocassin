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
import com.google.inject.name.Named;

public class GateDocumentDAOImpl implements GateDocumentDAO {

	private static final String GATE_DOCUMENT_AFFIX = ".tex.xml";
	private final String GATE_BUILTIN_CREOLE_DIR_PROPERTY;
	private final String GATE_HOME_PROPERTY;

	private final String GATE_DOCUMENT_LR_TYPE_PROPERTY;
	private final String GATE_STORAGE_DIR_PROPERTY;
	private final String ARXMLIV_MARKUP_NAME_PROPERTY;
	private final String TITLE_ANNOTATION_NAME_PROPERTY;
	private final String ARXMLIV_CREATOR_PROPERTY;

	private final Logger logger;

	private final AnnotationUtil annotationUtil;

	private boolean isInitialized = false;

	private SerialDataStore dataStore;

	@Inject
	public GateDocumentDAOImpl(
			Logger logger,
			AnnotationUtil annotationUtil,
			@Named("gate.home") String gateHome,
			@Named("gate.builtin.creole.dir") String gateBuiltinCreoleDir,
			@Named("gate.storage.dir") String gateStorageDir,
			@Named("gate.document.lr.type") String gateDocumentLrType,
			@Named("arxmliv.markup.name") String arxmlivMarkupName,
			@Named("title.annotation.name") String titleAnnotationName,
			@Named("arxmliv.creator.annotation.name") String arxmlivCreatorAnnotationName) {
		this.logger = logger;
		this.annotationUtil = annotationUtil;
		this.GATE_HOME_PROPERTY = gateHome;
		this.GATE_BUILTIN_CREOLE_DIR_PROPERTY = gateBuiltinCreoleDir;
		this.GATE_STORAGE_DIR_PROPERTY = gateStorageDir;
		this.GATE_DOCUMENT_LR_TYPE_PROPERTY = gateDocumentLrType;
		this.ARXMLIV_MARKUP_NAME_PROPERTY = arxmlivMarkupName;
		this.TITLE_ANNOTATION_NAME_PROPERTY = titleAnnotationName;
		this.ARXMLIV_CREATOR_PROPERTY = arxmlivCreatorAnnotationName;
		initialize();
	}

	private void initialize() {
		if (!isInitialized) {
			isInitialized = true;
			System.setProperty("gate.home", GATE_HOME_PROPERTY);
			System.setProperty("gate.builtin.creole.dir",
					GATE_BUILTIN_CREOLE_DIR_PROPERTY);
			try {
				Gate.init();
				this.dataStore = new SerialDataStore(GATE_STORAGE_DIR_PROPERTY);
				getDataStore().open();
			} catch (GateException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public void save(String documentId, File file)
			throws AccessGateStorageException,
			ru.ksu.niimm.cll.mocassin.nlp.gate.impl.PersistenceException {
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
	public GateDocumentMetadata loadMetadata(String documentId)
			throws AccessGateDocumentException, AccessGateStorageException {
		Document document = load(documentId);
		GateDocumentMetadata metadata = extractMetadata(document);
		release(document);
		return metadata;
	}

	@Override
	public Document load(String documentId) throws AccessGateDocumentException,
			AccessGateStorageException {
		List<String> documentIds = getDocumentIds(true);
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
			document = (Document) Factory.createResource(
					GATE_DOCUMENT_LR_TYPE_PROPERTY, features);
		} catch (ResourceInstantiationException e) {
			logger.log(Level.SEVERE, String.format(
					"couldn't create resource with id='%s' caused by: %s",
					documentId, e.getMessage()));
			throw new AccessGateDocumentException(e);
		}
		return document;
	}

	@Override
	public List<String> getDocumentIds() throws AccessGateDocumentException,
			AccessGateStorageException {
		return getDocumentIds(false);
	}

	private List<String> getDocumentIds(boolean isNativeName)
			throws AccessGateDocumentException {
		try {
			Iterator it = getDataStore().getLrIds(
					GATE_DOCUMENT_LR_TYPE_PROPERTY).iterator();
			List<String> gateDocuments = new ArrayList<String>();
			while (it.hasNext()) {
				String documentLrId = (String) it.next();
				gateDocuments.add(isNativeName ? documentLrId : documentLrId
						.substring(0,
								documentLrId.lastIndexOf(GATE_DOCUMENT_AFFIX)));
			}

			return gateDocuments;
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE,
					"couldn't get language resource identifiers");
			throw new AccessGateDocumentException(e);
		}
	}

	@Override
	public void release(Document document) {
		if (document != null) {
			Factory.deleteResource(document);
		}
	}

	private SerialDataStore getDataStore() {
		return dataStore;
	}

	private GateDocumentMetadata extractMetadata(Document document)
			throws AccessGateDocumentException {
		AnnotationSet allTitleAnnotations = document.getAnnotations(
				ARXMLIV_MARKUP_NAME_PROPERTY).get(
				TITLE_ANNOTATION_NAME_PROPERTY);
		String documentName = document.getName().substring(
				0,
				document.getName().lastIndexOf(GATE_DOCUMENT_AFFIX)
						+ GATE_DOCUMENT_AFFIX.length());
		if (allTitleAnnotations.size() == 0) {
			throw new AccessGateDocumentException(String.format(
					"The document %s has the wrong metadata: title is absent",
					documentName));
		}
		AnnotationSet allCreatorAnnotations = document.getAnnotations(
				ARXMLIV_MARKUP_NAME_PROPERTY).get(ARXMLIV_CREATOR_PROPERTY);
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
