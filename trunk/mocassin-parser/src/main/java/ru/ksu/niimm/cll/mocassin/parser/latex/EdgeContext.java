package ru.ksu.niimm.cll.mocassin.parser.latex;


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

	void setRefId(String refId);

	String getRefId();
}
