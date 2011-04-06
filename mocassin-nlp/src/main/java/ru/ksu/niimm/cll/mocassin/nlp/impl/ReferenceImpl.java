package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

public class ReferenceImpl implements Reference {
	private final int id;
	private final StructuralElement from;
	private final StructuralElement to;
	private final ParsedDocument document;
	private final String additionalRefid;
	private List<Token> sentenceTokens;
	private MocassinOntologyRelations relation;

	public static class Builder {
		private final int id;

		private StructuralElement from;
		private StructuralElement to;
		private ParsedDocument document;
		private String additionalRefid;

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

		public Builder document(ParsedDocument document) {
			this.document = document;
			return this;
		}

		public Builder additionalRefid(String additionalRefid) {
			this.additionalRefid = additionalRefid;
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
		this.document = builder.document;
		this.additionalRefid = builder.additionalRefid;
	}

	public List<Token> getSentenceTokens() {
		return sentenceTokens;
	}

	public void setSentenceTokens(List<Token> sentenceTokens) {
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

	public ParsedDocument getDocument() {
		return document;
	}

	public String getAdditionalRefid() {
		return additionalRefid;
	}

	@Override
	public MocassinOntologyRelations getPredictedRelation() {
		return relation;
	}

	@Override
	public void setPredictedRelation(MocassinOntologyRelations relation) {
		this.relation = relation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((document.getFilename() == null) ? 0 : document.hashCode());
		result = prime * result + id;
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
		ReferenceImpl other = (ReferenceImpl) obj;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s %s | %s", from.getPredictedClass(), to
				.getPredictedClass(), relation);
	}

}
