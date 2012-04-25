package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import java.util.List;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.OutlineNode;
import net.sourceforge.texlipse.model.TexCommandEntry;

public class LatexDocumentModel {
	private OutlineNode documentRoot;
	private List<OutlineNode> tree;
	private String docId;
	/**
	 * list of references ordered by their "key" values
	 */
	private List<DocumentReference> references;
	/**
	 * list of labels ordered by their position (beginLine/offset) values
	 */
	private List<PdfReferenceEntry> labels;

	private List<TexCommandEntry> commands;

	private List<NewtheoremCommand> newtheorems;
	/**
	 * flag for numbering theorem-like environments
	 */
	private boolean isNumberingWithinSection;

	public LatexDocumentModel(List<OutlineNode> tree) {
		this.tree = tree;
	}

	public List<OutlineNode> getTree() {
		return tree;
	}

	public void setTree(List<OutlineNode> tree) {
		this.tree = tree;
	}

	public List<DocumentReference> getReferences() {
		return references;
	}

	public void setReferences(List<DocumentReference> references) {
		this.references = references;
	}

	public List<PdfReferenceEntry> getLabels() {
		return labels;
	}

	public void setLabels(List<PdfReferenceEntry> labels) {
		this.labels = labels;
	}

	public OutlineNode getDocumentRoot() {
		return documentRoot;
	}

	public void setDocumentRoot(OutlineNode documentRoot) {
		this.documentRoot = documentRoot;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public List<TexCommandEntry> getCommands() {
		return commands;
	}

	public void setCommands(List<TexCommandEntry> commands) {
		this.commands = commands;
	}

	public List<NewtheoremCommand> getNewtheorems() {
		return newtheorems;
	}

	public void setNewtheorems(List<NewtheoremCommand> newtheorems) {
		this.newtheorems = newtheorems;
	}

	public boolean isNumberingWithinSection() {
		return isNumberingWithinSection;
	}

	public void setNumberingWithinSection(boolean isNumberingWithinSection) {
		this.isNumberingWithinSection = isNumberingWithinSection;
	}

}
