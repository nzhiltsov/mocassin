package ru.ksu.niimm.cll.mocassin.analyzer.relation.impl;

import gate.Document;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.RelationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

public class ExemplifiesRelationAnalyzerImpl extends AbstractRelationAnalyzer
		implements ExemplifiesRelationAnalyzer {

	/**
	 * main algorithm implementation
	 * 
	 * @param document
	 * @param info
	 */
	public RelationInfo processRelationInfo(Document document, RelationInfo info) {
		StructuralElement rangeElement = getStructuralElementSearcher()
				.findById(document, info.getRangeId());

		MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
				.getValidRanges(MocassinOntologyRelations.EXEMPLIFIES);

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
