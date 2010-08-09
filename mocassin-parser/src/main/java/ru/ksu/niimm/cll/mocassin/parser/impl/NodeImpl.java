package ru.ksu.niimm.cll.mocassin.parser.impl;

import ru.ksu.niimm.cll.mocassin.parser.Node;

public class NodeImpl implements Node {
	private String id;
	private String name;
	private String labelText;

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

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return String.format("\"%s/ %s\"", getId(), getName());
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

}
