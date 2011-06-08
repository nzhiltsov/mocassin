package ru.ksu.niimm.cll.mocassin.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QueryTree extends Composite {

	private static QueryTreeUiBinder uiBinder = GWT
			.create(QueryTreeUiBinder.class);

	interface QueryTreeUiBinder extends UiBinder<VerticalPanel, QueryTree> {
	}

	@UiField
	Tree tree;

	public QueryTree() {
		initWidget(uiBinder.createAndBindUi(this));
		initializeRoot();
	}

	private void initializeRoot() {
		TreeItem root = new TreeItem();
		this.tree.addItem(root);
		root.setWidget(new ConceptTreeNode(root));
	}

	public void clearTree() {
		tree.clear();
		initializeRoot();
	}

	public TreeItem getRoot() {
		return tree.getItem(0);
	}

}
