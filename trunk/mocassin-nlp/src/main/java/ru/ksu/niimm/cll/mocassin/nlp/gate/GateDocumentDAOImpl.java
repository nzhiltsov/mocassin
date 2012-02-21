package ru.ksu.niimm.cll.mocassin.nlp.gate;

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

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.util.GateDocumentMetadata;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;

class GateDocumentDAOImpl implements GateDocumentDAO {

	private static final String GATE_DOCUMENT_AFFIX = ".tex.xml";
	private final String GATE_BUILTIN_CREOLE_DIR_PROPERTY;
	private final String GATE_HOME_PROPERTY;

	private final String GATE_DOCUMENT_LR_TYPE_PROPERTY;
	private final String GATE_STORAGE_DIR_PROPERTY;
	private final String ARXMLIV_MARKUP_NAME_PROPERTY;
	private final String TITLE_ANNOTATION_NAME_PROPERTY;
	private final String ARXMLIV_CREATOR_PROPERTY;
	@InjectLogger
	private Logger logger;

	private final AnnotationUtil annotationUtil;

	private SerialDataStore dataStore;

	@Inject
	GateDocumentDAOImpl(
			AnnotationUtil annotationUtil,
			@Named("gate.home") String gateHome,
			@Named("gate.builtin.creole.dir") String gateBuiltinCreoleDir,
			@Named("gate.storage.dir") String gateStorageDir,
			@Named("gate.document.lr.type") String gateDocumentLrType,
			@Named("arxmliv.markup.name") String arxmlivMarkupName,
			@Named("title.annotation.name") String titleAnnotationName,
			@Named("arxmliv.creator.annotation.name") String arxmlivCreatorAnnotationName) {
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

	@Override
	public void save(String documentId, File file, String encoding)
			throws AccessGateStorageException,
			ru.ksu.niimm.cll.mocassin.nlp.gate.PersistenceException {
		try {
			Document document = Factory.newDocument(file.toURI().toURL(),
					encoding);
			Document persistedDocument = (Document) this.dataStore.adopt(
					document, null);
			persistedDocument.setName(StringUtil.arxivid2filename(documentId,
					"tex.xml"));
			this.dataStore.sync(persistedDocument);
		} catch (ResourceInstantiationException e) {
			logger.error("Failed to create a GATE document for the file='{}'",
					file.getAbsolutePath(), e);
			throw new ru.ksu.niimm.cll.mocassin.nlp.gate.PersistenceException(e);
		} catch (MalformedURLException e) {
			logger.error(
					"Failed to create a GATE document for the file='{}': this file path probably is incorrect",
					file.getAbsolutePath(), e);
			throw new IllegalArgumentException(e);
		} catch (PersistenceException e) {
			logger.error("Failed to save a GATE document for the file='{}'",
					file.getAbsolutePath(), e);
			throw new ru.ksu.niimm.cll.mocassin.nlp.gate.PersistenceException(e);
		} catch (SecurityException e) {
			logger.error(
					"Failed to save a GATE document for the file='{}' due to security reasons",
					file.getAbsolutePath(), e);
			throw new ru.ksu.niimm.cll.mocassin.nlp.gate.PersistenceException(e);
		}
	}

	@Override
	public void delete(String documentId) throws AccessGateDocumentException,
			ru.ksu.niimm.cll.mocassin.nlp.gate.PersistenceException {
		List<String> documentIds = getDocumentIds(true);
		String foundDocumentId = Iterables.find(documentIds,
				new DocumentNamePredicate(documentId), null);
		if (foundDocumentId == null) {
			logger.warn("The document='{}' asked for deleting does not exist.",
					documentId);
			return;
		}
		try {
			this.dataStore.delete(GATE_DOCUMENT_LR_TYPE_PROPERTY,
					foundDocumentId);
		} catch (PersistenceException e) {
			logger.error("Failed to delete a GATE document with id='{}'",
					documentId, e);
			throw new ru.ksu.niimm.cll.mocassin.nlp.gate.PersistenceException(e);
		}
	}

	@Override
	public GateDocumentMetadata loadMetadata(String documentId)
			throws AccessGateDocumentException, AccessGateStorageException {
		Document document = load(documentId);
		GateDocumentMetadata metadata;
		try {
			metadata = extractMetadata(document);
		} finally {
			release(document);
		}

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
			logger.error("Couldn't create a resource with id='{}'", documentId,
					e);
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
			logger.error("Couldn't get language resource identifiers", e);
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
		String[] strTokens = annotationUtil.getTokensWithMathAnnotation(
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
