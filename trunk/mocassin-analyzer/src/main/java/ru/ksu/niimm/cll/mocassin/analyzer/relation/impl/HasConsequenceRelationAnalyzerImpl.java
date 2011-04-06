package ru.ksu.niimm.cll.mocassin.analyzer.relation.impl;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;


public class HasConsequenceRelationAnalyzerImpl extends AbstractRelationAnalyzer implements
		HasConsequenceRelationAnalyzer {
	/**
	 * main algorithm implementation
	 * 
	 * @param document
	 * @param info
	 */
	public RelationInfo processRelationInfo(ParsedDocument document,
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
}
