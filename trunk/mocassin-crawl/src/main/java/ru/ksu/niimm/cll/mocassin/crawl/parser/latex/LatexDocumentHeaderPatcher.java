package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

/**
 * This patcher adds necessary package declarations to the header of a given
 * document
 * 
 * @author nzhiltsov
 * 
 */
public interface LatexDocumentHeaderPatcher {
	void patch(String arxivId);
}
