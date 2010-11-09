package ru.ksu.niimm.cll.mocassin.analyzer.relation;

import java.util.List;

/**
 * This analyzer detects the structural element as a domain of 'hasConsequence'
 * relation (Mocassin Ontology) for each given range
 * 
 * @author nzhiltsov
 * 
 */
public interface HasConsequenceRelationAnalyzer {
	List<RelationInfo> analyze(List<RelationInfo> relations);
}
