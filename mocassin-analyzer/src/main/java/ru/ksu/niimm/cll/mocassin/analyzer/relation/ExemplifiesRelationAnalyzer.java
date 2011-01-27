package ru.ksu.niimm.cll.mocassin.analyzer.relation;

import java.util.List;
/**
 * This analyzer detects the structural element as a range of 'exemplifies'
 * relation (Mocassin Ontology) for each given domain
 * 
 * @author nzhiltsov
 * 
 */
public interface ExemplifiesRelationAnalyzer {
	List<RelationInfo> analyze(List<RelationInfo> relations);
}
