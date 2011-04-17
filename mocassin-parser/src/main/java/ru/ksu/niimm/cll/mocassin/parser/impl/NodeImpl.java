package ru.ksu.niimm.cll.mocassin.parser.impl;

import java.io.Serializable;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import ru.ksu.niimm.cll.mocassin.parser.Node;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeImpl implements Node {
	private String id;
	@XmlElement
	private String name;
	private String contents;
	private int beginLine;
	private int endLine;
	private int offset;
	@XmlTransient
	private String labelText;

	private NodeImpl() {
	}

	public NodeImpl(String id, String name) {
		this.id = id;
		this.name = name.endsWith("*") ? name.substring(0, name.length() - 1)
				: name;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public String getId() {
		return id;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public int getBeginLine() {
		return beginLine;
	}

	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeImpl other = (NodeImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@SuppressWarnings("serial")
	public static class NodePositionComparator implements Serializable,
			Comparator<Node> {

		@Override
		public int compare(Node first, Node second) {
			if (first.getBeginLine() < second.getBeginLine())
				return -1;
			if (first.getBeginLine() > second.getBeginLine())
				return 1;
			if (first.getOffset() < second.getOffset())
				return -1;
			if (first.getOffset() > second.getOffset())
				return 1;
			return 0;
		}
	}
}
