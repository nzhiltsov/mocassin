package ru.ksu.niimm.cll.mocassin.parser.latex;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ru.ksu.niimm.cll.mocassin.parser.impl.adapters.EdgeAdapter;

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
