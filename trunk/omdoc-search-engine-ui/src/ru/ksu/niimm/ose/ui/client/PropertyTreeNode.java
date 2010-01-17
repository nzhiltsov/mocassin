package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class PropertyTreeNode extends Composite {

	private static PropertyTreeNodeUiBinder uiBinder = GWT
			.create(PropertyTreeNodeUiBinder.class);

	interface PropertyTreeNodeUiBinder extends
			UiBinder<Widget, PropertyTreeNode> {
	}

	TreeItem treeItem;

	public PropertyTreeNode(TreeItem treeItem) {
		initWidget(uiBinder.createAndBindUi(this));
		this.treeItem = treeItem;
	}

}
