package ru.ksu.niimm.cll.mocassin.parser;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ru.ksu.niimm.cll.mocassin.parser.impl.adapters.EdgeContextAdapter;

/**
 * Context that accompanies with relation
 * 
 * @author nzhiltsov
 * 
 */
@XmlJavaTypeAdapter(EdgeContextAdapter.class)
public interface EdgeContext {
	EdgeType getEdgeType();

	String getAroundText();

	void setAroundText(String aroundText);

	void setRefId(String refId);

	String getRefId();
}
