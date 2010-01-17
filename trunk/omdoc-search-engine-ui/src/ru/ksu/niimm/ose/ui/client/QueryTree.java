package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QueryTree extends Composite {

	private static QueryTreeUiBinder uiBinder = GWT
			.create(QueryTreeUiBinder.class);

	interface QueryTreeUiBinder extends UiBinder<VerticalPanel, QueryTree> {
	}

	@UiField
	Tree tree;

	public QueryTree() {
		initWidget(uiBinder.createAndBindUi(this));
		TreeItem root = new TreeItem();
		root.setWidget(new ConceptTreeNode(root));
		this.tree.addItem(root);
	}
}
