package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import edu.uci.ics.jung.graph.Graph;

import java.util.List;

/**
 * The analyzer extracts the relevant statistics information about the document
 * graph using word based metrics
 *
 * @author Nikita Zhiltsov
 * @author Azat Khasanshin
 *
 */
public interface WordAnalyzer {
    /**
     * returns structural elements with word features extracted
     * @param graph
     * @return
     */
    List<WordFeatureInfo> extractWordFeatures(Graph<StructuralElement, Reference> graph);
}
