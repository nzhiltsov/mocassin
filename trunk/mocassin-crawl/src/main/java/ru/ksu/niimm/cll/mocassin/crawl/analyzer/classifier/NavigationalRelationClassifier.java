package ru.ksu.niimm.cll.mocassin.crawl.analyzer.classifier;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
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
