package ru.ksu.niimm.cll.mocassin.nlp;

public class Citation {
	/**
	 * e.g. Mathnet key or arXiv id of the cited document
	 */
	private final String sourceDocumentId;
	/**
	 * e.g. Mathnet key or arXiv id of the cited document
	 */
	private final String destDocumentId;
	/**
	 * order number in the references list; starts with 1
	 */
	private final int number;
	/**
	 * text contents of the citation sentence(s)
	 */
	private final String anchorText;

	private Citation(Builder builder) {
		this.sourceDocumentId = builder.sourceDocumentId;
		this.destDocumentId = builder.destDocumentId;
		this.number = builder.number;
		this.anchorText = builder.anchorText;
	}

	public static class Builder {
		private final String sourceDocumentId;
		private String destDocumentId;
		private int number;
		private String anchorText;

		public Builder(String sourceDocumentId) {
			this.sourceDocumentId = sourceDocumentId;
		}

		public Builder destDocumentId(String destDocumentId) {
			this.destDocumentId = destDocumentId;
			return this;
		}

		public Builder number(int number) {
			this.number = number;
			return this;
		}

		public Builder anchorText(String anchorText) {
			this.anchorText = anchorText;
			return this;
		}

		public Citation build() {
			return new Citation(this);
		}
	}

	public String getDestinationId() {
		return destDocumentId;
	}

	public int getNumber() {
		return number;
	}

	public String getAnchorText() {
		return anchorText;
	}

	public String getSourceDocumentId() {
		return sourceDocumentId;
	}

}