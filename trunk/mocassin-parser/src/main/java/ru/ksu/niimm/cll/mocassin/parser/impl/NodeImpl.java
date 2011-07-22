package ru.ksu.niimm.cll.mocassin.parser.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.Node;

import com.google.common.base.Predicate;

public class NodeImpl implements Node {
	private final String id;
	private final String name;
	private final List<String> contents = new LinkedList<String>();
	private final int beginLine;
	private final int endLine;
	private final int offset;
	private final boolean isEnvironment;
	private final boolean isNumbered;
	private String labelText;
	private String title;
	private int pdfPageNumber;

	public static class Builder {
		private final String id;
		private final String name;
		private boolean isNumbered;

		private int beginLine;
		private int endLine;
		private int offset;
		private boolean isEnvironment;
		private String labelText;
		private String title;
		private int pdfPageNumber;

		public Builder(String id, String name) {
			this.id = id;
			if (name.endsWith("*")) {
				this.name = name.substring(0, name.length() - 1);
			} else {
				this.name = name;
			}

		}

		public Builder pdfPageNumber(int pageNumber) {
			this.pdfPageNumber = pageNumber;
			return this;
		}

		public Builder beginLine(int beginLine) {
			this.beginLine = beginLine;
			return this;
		}

		public Builder endLine(int endLine) {
			this.endLine = endLine - 1;
			return this;
		}

		public Builder offset(int offset) {
			this.offset = offset;
			return this;
		}

		public Builder isEnvironment(boolean isEnvironment) {
			this.isEnvironment = isEnvironment;
			return this;
		}

		public Builder numbered(boolean isNumbered) {
			this.isNumbered = isNumbered;
			return this;
		}

		public Builder labelText(String labelText) {
			this.labelText = labelText;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Node build() {
			return new NodeImpl(this);
		}
	}

	private NodeImpl(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.isNumbered = builder.isNumbered;
		this.beginLine = builder.beginLine;
		this.endLine = builder.endLine;
		this.offset = builder.offset;
		this.isEnvironment = builder.isEnvironment;
		this.labelText = builder.labelText;
		this.title = builder.title;
		this.pdfPageNumber = builder.pdfPageNumber;
	}

	public String getLabelText() {
		return labelText;
	}

	public String getId() {
		return id;
	}

	public List<String> getContents() {
		return contents;
	}

	public void addContents(String... tokens) {
		Collections.addAll(this.contents, tokens);
	}

	public int getBeginLine() {
		return beginLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public int getOffset() {
		return offset;
	}

	public boolean isEnvironment() {
		return isEnvironment;
	}

	public String getName() {
		return this.name;
	}

	public String getTitle() {
		return title;
	}

	public int getPdfPageNumber() {
		return pdfPageNumber;
	}

	public void setPdfPageNumber(int pdfPageNumber) {
		this.pdfPageNumber = pdfPageNumber;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public boolean isNumbered() {
		return this.isNumbered;
	}

	@Override
	public String toString() {
		return String.format("%s %s", this.id, this.name);
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

	public static class EnclosingNodePredicate implements Predicate<Node> {
		private int lineNumber;

		public EnclosingNodePredicate(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		@Override
		public boolean apply(Node node) {
			return node.getBeginLine() < this.lineNumber
					&& node.getEndLine() > this.lineNumber + 1;
		}

	}

	public static class NodeBoundaryPredicate implements Predicate<Node> {
		private int lineNumber;

		public NodeBoundaryPredicate(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		@Override
		public boolean apply(Node node) {
			return node.getBeginLine() == this.lineNumber
					|| node.getEndLine() - 1 == this.lineNumber;
		}

	}
}
