package ru.ksu.niimm.cll.mocassin.analyzer.relation.impl;

import gate.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class HasConsequenceRelationAnalyzerImpl implements
		HasConsequenceRelationAnalyzer {
	@Inject
	private Logger logger;
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private StructuralElementSearcher structuralElementSearcher;

	@Override
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
		Map<String, String> prefix2id = mapPrefixWithIds(documentIds, prefixSet);

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

	/**
	 * main algorithm implementation
	 * 
	 * @param document
	 * @param info
	 */
	private RelationInfo processRelationInfo(Document document,
			RelationInfo info) {
		StructuralElement rangeElement = getStructuralElementSearcher()
				.findById(document, info.getRangeId());
		MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
				.getValidDomains(MocassinOntologyRelations.HAS_CONSEQUENCE);

		StructuralElement predecessor = getStructuralElementSearcher()
				.findClosestPredecessor(document, rangeElement.getId(),
						validDomains);
		RelationInfo definedInfo = new RelationInfo();
		definedInfo.setFilename(info.getFilename());
		definedInfo.setRelation(info.getRelation());
		definedInfo.setRangeId(info.getRangeId());
		definedInfo.setDomainId(predecessor.getId());
		return definedInfo;
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	private Map<String, String> mapPrefixWithIds(List<String> documentIds,
			Set<String> prefixSet) {
		Collections.sort(documentIds);

		ArrayList<String> docPrefixes = new ArrayList<String>(prefixSet);
		Collections.sort(docPrefixes);

		Map<String, String> prefix2id = Maps.newHashMap();
		int j = 0;
		for (String docPrefix : docPrefixes) {
			while (j < documentIds.size()) {
				String documentId = documentIds.get(j);
				if (documentId.startsWith(docPrefix)) {
					prefix2id.put(docPrefix, documentId);
					break;
				}
				j++;
			}
		}
		return prefix2id;
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
