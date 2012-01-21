package ru.ksu.niimm.cll.mocassin.parser;

import java.util.List;

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

	List<String> getContents();

	void addContents(String... text);

	int getBeginLine();

	int getEndLine();

	int getOffset();

	boolean isEnvironment();

	String getTitle();

	void setTitle(String title);
	
	boolean isNumbered();
	
}