package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface GraphEditableAnalyzer {

    /**
     * Adds relation instances of the Mocassin Ontology to a given
     * document graph
     * 
     * @param graph
     *            graph
     * @param document
     *            URL, e.g. 'http://mathnet.ru/ivm18'
     */
    void addRelations(Graph<StructuralElement, Reference> graph, String paperUrl);

}