package ru.ksu.niimm.cll.mocassin.analyzer.classifier;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface NavigationalRelationClassifier {
	/**
	 * predict the relation of a given reference
	 * 
	 * @param reference
	 * @param graph
	 *            a graph that contains a given reference
	 * @return
	 */
	Prediction predict(Reference reference,
			Graph<StructuralElement, Reference> graph);
}
