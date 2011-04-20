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

	void setBeginLine(int beginLine);

	int getBeginLine();

	int getEndLine();

	void setEndLine(int endLine);

	int getOffset();

	void setOffset(int offset);

	boolean isEnvironment();

	void setEnvironment(boolean isEnvironment);
}
