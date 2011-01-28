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
		StructuralElement domainElement = getStructuralElementSearcher()
				.findById(document, info.getDomainId());

		MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
				.getValidRanges(MocassinOntologyRelations.EXEMPLIFIES);

		StructuralElement predecessor = getStructuralElementSearcher()
				.findClosestPredecessor(document, domainElement.getId(),
						validDomains);
		RelationInfo definedInfo = new RelationInfo();
		definedInfo.setFilename(info.getFilename());
		definedInfo.setRelation(info.getRelation());
		definedInfo.setRangeId(predecessor.getId());
		definedInfo.setDomainId(domainElement.getId());
		return definedInfo;
	}

}
