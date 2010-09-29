package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;

public class ReferenceImpl implements Reference {
	private final int id;
	private final StructuralElement from;
	private final StructuralElement to;
	private final String documentName;
	private final String additionalRefid;
	private List<String> sentenceTokens;

	public static class Builder {
		private final int id;

		private StructuralElement from;
		private StructuralElement to;
		private String documentName;
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

		public Builder document(String documentName) {
			this.documentName = documentName;
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
		this.documentName = builder.documentName;
		this.additionalRefid = builder.additionalRefid;
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

	public String getAdditionalRefid() {
		return additionalRefid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((documentName == null) ? 0 : documentName.hashCode());
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
		if (documentName == null) {
			if (other.documentName != null)
				return false;
		} else if (!documentName.equals(other.documentName))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s ", documentName.substring(0,
				documentName.indexOf(".")), id, additionalRefid);
	}

}
