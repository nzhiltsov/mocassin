package ru.ksu.niimm.cll.mocassin.parser;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ru.ksu.niimm.cll.mocassin.parser.impl.adapters.NodeAdapter;

/**
 * 
 * Document part
 * 
 * @author nzhiltsov
 * 
 */
@XmlJavaTypeAdapter(NodeAdapter.class)
public interface Node {

	String getId();

	String getName();

	boolean equals(Object o);

	int hashCode();

	String getLabelText();

	void setLabelText(String labelText);
}
