package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.Token;

public class StructuralElementImpl implements StructuralElement {
	private final int id;
	private final long start;
	private final long end;
	private final String name;
	private String label;
	private List<Token> titleTokens;

	public static class Builder {
		private final int id;

		private long start;
		private long end;
		private String name;

		public Builder(int id) {
			this.id = id;
		}

		public Builder start(long start) {
			this.start = start;
			return this;
		}

		public Builder end(long end) {
			this.end = end;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public StructuralElement build() {
			return new StructuralElementImpl(this);
		}
	}

	public StructuralElementImpl(Builder builder) {
		this.id = builder.id;
		this.start = builder.start;
		this.end = builder.end;
		this.name = builder.name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Token> getTitleTokens() {
		return titleTokens;
	}

	public void setTitleTokens(List<Token> titleTokens) {
		this.titleTokens = titleTokens;
	}

	public int getId() {
		return id;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Token token : titleTokens) {
			sb.append(token.toString());
			sb.append(" ");
		}
		return String.format("%d; %s; %s", id, name, sb.toString());
	}

}
