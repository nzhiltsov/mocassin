package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;


/**
 * Relation between document parts
 * 
 * @author nzhiltsov
 * 
 */
public interface Edge {
	void setContext(EdgeContext edgeContext);

	EdgeContext getContext();

}
