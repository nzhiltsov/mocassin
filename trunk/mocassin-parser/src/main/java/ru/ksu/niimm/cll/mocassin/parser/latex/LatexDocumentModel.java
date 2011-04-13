package ru.ksu.niimm.cll.mocassin.parser.latex;

import java.util.List;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.OutlineNode;
import net.sourceforge.texlipse.model.ReferenceEntry;

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
	private List<ReferenceEntry> labels;

	private List<TexCommandEntryAdapter> commands;

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

	public List<ReferenceEntry> getLabels() {
		return labels;
	}

	public void setLabels(List<ReferenceEntry> labels) {
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

	public List<TexCommandEntryAdapter> getCommands() {
		return commands;
	}

	public void setCommands(List<TexCommandEntryAdapter> commands) {
		this.commands = commands;
	}

}
