package ru.ksu.niimm.cll.mocassin.nlp;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

class GateCitationSearcher implements CitationSearcher {
	private final String ARXMLIV_MARKUP_NAME;
	private final String ARXMLIV_CITE_ANNOTATION_NAME;

	private final Logger logger;

	private final GateDocumentDAO gateDocumentDAO;

	private final AnnotationUtil annotationUtil;

	@Inject
	GateCitationSearcher(
			Logger logger,
			GateDocumentDAO gateDocumentDAO,
			AnnotationUtil annotationUtil,
			@Named("arxmliv.markup.name") String arxmlivMarkupName,
			@Named("arxmliv.cite.annotation.name") String arxmlivCiteAnnotationName) {
		this.ARXMLIV_MARKUP_NAME = arxmlivMarkupName;
		this.ARXMLIV_CITE_ANNOTATION_NAME = arxmlivCiteAnnotationName;
		this.logger = logger;
		this.gateDocumentDAO = gateDocumentDAO;
		this.annotationUtil = annotationUtil;
	}

	@Override
	public List<String> getCitationSentences(String documentId) {
		String gateDocumentId = StringUtil.arxivid2gateid(documentId);
		Document gateDocument = null;
		try {
			gateDocument = gateDocumentDAO.load(gateDocumentId);
			AnnotationSet citations = gateDocument.getAnnotations(
					ARXMLIV_MARKUP_NAME).get(ARXMLIV_CITE_ANNOTATION_NAME);
			return extractSentences(gateDocument, citations);
		} catch (AccessGateDocumentException e) {
			logger.log(Level.SEVERE, String.format(
					"failed to load the document: %s", documentId));
			throw new RuntimeException(e);
		} catch (AccessGateStorageException e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"failed to access the storage while loading the document: %s",
							documentId));
			throw new RuntimeException(e);
		} finally {
			gateDocumentDAO.release(gateDocument);
		}
	}

	private List<String> extractSentences(Document gateDocument,
			AnnotationSet citations) {
		List<Annotation> sortedCitations = new ArrayList<Annotation>(citations);
		Collections.sort(sortedCitations);
		List<String> citationSentences = new LinkedList<String>();
		for (Annotation citation : sortedCitations) {
			Annotation enclosingSentence = annotationUtil.getEnclosingSentence(
					gateDocument, citation);
			String[] citationSentenceTokens = annotationUtil
					.getTokensWithTemplatedMathAnnotations(gateDocument,
							enclosingSentence, '$');
			StringBuilder sb = new StringBuilder();
			for (String str : citationSentenceTokens) {
				sb.append(String.format("%s ", str));
			}
			citationSentences.add(sb.toString());
		}
		return citationSentences;
	}

}
