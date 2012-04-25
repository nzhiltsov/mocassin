package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

/**
 * Relation types
 * 
 * @author nzhiltsov
 * 
 */
public enum EdgeType {
	/**
	 * this relation type is about one node contains another, e.g. a section
	 * contains a proposition
	 */
	CONTAINS,
	/**
	 * this relation type concerns the relation between a reference and the
	 * label that it refers to
	 */
	REFERS_TO
}
