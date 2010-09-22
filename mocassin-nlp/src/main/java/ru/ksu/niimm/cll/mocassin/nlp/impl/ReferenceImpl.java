package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;

public class ReferenceImpl implements Reference {
	private final int id;
	private final StructuralElement from;
	private final StructuralElement to;
	private final String documentName;
	private List<String> sentenceTokens;

	public static class Builder {
		private final int id;

		private StructuralElement from;
		private StructuralElement to;
		private String documentName;

		public Builder(int id) {
			this.id = id;
		}

		public Builder from(StructuralElement from) {
			this.from = from;
			return this;
		}

		public Builder to(StructuralElement to) {
			this.to = to;
			return this;
		}

		public Builder document(String documentName) {
			this.documentName = documentName;
			return this;
		}

		public Reference build() {
			return new ReferenceImpl(this);
		}
	}

	public ReferenceImpl(Builder builder) {
		this.id = builder.id;
		this.from = builder.from;
		this.to = builder.to;
		this.documentName = builder.documentName;
	}

	public List<String> getSentenceTokens() {
		return sentenceTokens;
	}

	public void setSentenceTokens(List<String> sentenceTokens) {
		this.sentenceTokens = sentenceTokens;
	}

	public int getId() {
		return id;
	}

	public StructuralElement getFrom() {
		return from;
	}

	public StructuralElement getTo() {
		return to;
	}

	public String getDocumentName() {
		return documentName;
	}

}
