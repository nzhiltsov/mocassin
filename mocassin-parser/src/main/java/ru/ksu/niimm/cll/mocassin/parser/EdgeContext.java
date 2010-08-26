package ru.ksu.niimm.cll.mocassin.parser;

/**
 * Context that accompanies with relation
 * 
 * @author nzhiltsov
 * 
 */
public interface EdgeContext {
	EdgeType getEdgeType();

	String getAroundText();

	void setAroundText(String aroundText);
}
