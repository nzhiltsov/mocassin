package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

public class StructuralElementImpl implements StructuralElement {
	private final int id;
	private final String uri;
	private final long start;
	private final long end;
	private final String name;
	private List<String> labels;
	private List<Token> contentTokens;
	private List<Token> titleTokens;
	private int startPageNumber;
	private MocassinOntologyClasses predictedClass;

	public static class Builder {
		private final int id;

		private String uri;
		private long start;
		private long end;
		private String name;

		public Builder(int id) {
			this.id = id;
		}

		public Builder uri(String uri) {
			this.uri = uri;
			return this;
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
		this.uri = builder.uri;
		this.start = builder.start;
		this.end = builder.end;
		this.name = builder.name;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
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

	public String getUri() {
		return uri;
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

	public MocassinOntologyClasses getPredictedClass() {
		return predictedClass;
	}

	public void setPredictedClass(MocassinOntologyClasses predictedClass) {
		this.predictedClass = predictedClass;
	}

	public List<Token> getContentTokens() {
		return contentTokens;
	}

	public void setContentTokens(List<Token> contentTokens) {
		this.contentTokens = contentTokens;
	}

	public int getStartPageNumber() {
		return startPageNumber;
	}

	public void setStartPageNumber(int startPageNumber) {
		this.startPageNumber = startPageNumber;
	}

	@Override
	public String toTitleString() {
		StringBuilder sb = new StringBuilder();
		for (Token token : titleTokens) {
			sb.append(token.toString());
			sb.append(" ");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return String.format("%s; %s", uri, predictedClass);
	}
}
