package ru.ksu.niimm.cll.mocassin.analyzer.relation.impl;

import gate.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

public abstract class AbstractRelationAnalyzer {

	@Inject
	private Logger logger;
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private StructuralElementSearcher structuralElementSearcher;

	public AbstractRelationAnalyzer() {
		super();
	}

	public List<RelationInfo> analyze(List<RelationInfo> relations) {
		List<RelationInfo> processedInfoList = new ArrayList<RelationInfo>();
		Map<String, List<RelationInfo>> filename2relations = groupByFilename(relations);
		List<String> documentIds;
		try {
			documentIds = getGateDocumentDAO().getDocumentIds();
		} catch (Exception e) {
			throw new RuntimeException("couldn't get list of documents", e);
		}
		Set<String> prefixSet = filename2relations.keySet();
		Map<String, String> prefix2id = CollectionUtil.mapPrefixesWithNames(documentIds, prefixSet);

		for (String filename : prefix2id.keySet()) {
			String documentId = prefix2id.get(filename);
			try {
				Document document = getGateDocumentDAO().load(documentId);
				List<RelationInfo> relationInfoList = filename2relations
						.get(filename);
				for (RelationInfo info : relationInfoList) {
					processedInfoList.add(processRelationInfo(document, info));
				}
				getGateDocumentDAO().release(document);

			} catch (Exception e) {
				logger.log(Level.SEVERE, String.format(
						"failed to load document: %s", documentId));
			}
		}
		return processedInfoList;
	}

	protected abstract RelationInfo processRelationInfo(Document document,
			RelationInfo info);

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

	private Map<String, List<RelationInfo>> groupByFilename(
			List<RelationInfo> relations) {
		Map<String, List<RelationInfo>> filename2relations = Maps.newHashMap();

		for (RelationInfo relation : relations) {
			String filename = relation.getFilename();
			if (filename2relations.containsKey(filename)) {
				List<RelationInfo> list = filename2relations.get(filename);
				list.add(relation);
			} else {
				List<RelationInfo> list = new ArrayList<RelationInfo>();
				list.add(relation);
				filename2relations.put(filename, list);
			}
		}
		return filename2relations;
	}

}