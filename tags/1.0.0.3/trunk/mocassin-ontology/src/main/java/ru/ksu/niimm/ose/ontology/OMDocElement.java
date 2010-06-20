package ru.ksu.niimm.ose.ontology;

public class OMDocElement {
	/**
	 * URI of OMDoc resource
	 * 
	 */
	private String resourceUri;
	/**
	 * element id in OMDoc resource
	 */
	private String id;
	/**
	 * TeX source file info 
	 */
	private SourceReference srcRef;
	/**
	 * full file name of PDF
	 * e.g. file:/collections/example.pdf
	 */
	private String pdfFileName;
	/**
	 * metadata of the article that this OMDoc resource contains
	 */
	private ArticleMetadata articleMetadata;

	public OMDocElement(String resourceUri, String id, SourceReference srcRef) {
		this.resourceUri = resourceUri;
		this.id = id;
		this.srcRef = srcRef;
	}

	public String getResourceUri() {
		return resourceUri;
	}

	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SourceReference getSrcRef() {
		return srcRef;
	}

	public void setSrcRef(SourceReference srcRef) {
		this.srcRef = srcRef;
	}

	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	public ArticleMetadata getArticleMetadata() {
		return articleMetadata;
	}

	public void setArticleMetadata(ArticleMetadata articleMetadata) {
		this.articleMetadata = articleMetadata;
	}

}
