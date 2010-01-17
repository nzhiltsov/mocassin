package ru.ksu.niimm.ose.ui.client;

import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestBox2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ConceptTreeNode extends Composite {

	private static QueryTreeNodeUiBinder uiBinder = GWT
			.create(QueryTreeNodeUiBinder.class);

	interface QueryTreeNodeUiBinder extends
			UiBinder<HorizontalPanel, ConceptTreeNode> {
	}

	@UiField
	OntologyElementSuggestBox suggestBox;
	@UiField
	Button addButton;

	TreeItem treeItem;

	public ConceptTreeNode(TreeItem treeItem) {
		initWidget(uiBinder.createAndBindUi(this));
		this.treeItem = treeItem;
	}

	@UiHandler("addButton")
	void handleClick(ClickEvent event) {
		TreeItem child = new TreeItem();
		child.setWidget(new PropertyTreeNode(child));
		treeItem.addItem(child);
		child.getTree().setSelectedItem(child);
		child.getTree().ensureSelectedItemVisible();
	}
}
