package ru.ksu.niimm.cll.mocassin.analyzer.relation;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author nzhiltsov
 * 
 */
public interface HasConsequenceRelationAnalyzer {
	/**
	 * adds instances of 'hasConsequence' (Mocassin Ontology) to a given document graph
	 * 
	 * @param graph
	 * @param document 
	 */
	void addRelations(Graph<StructuralElement, Reference> graph, ParsedDocument document);
}
