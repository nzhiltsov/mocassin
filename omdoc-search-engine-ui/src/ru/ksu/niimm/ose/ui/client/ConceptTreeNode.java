package ru.ksu.niimm.ose.ui.client;

import java.util.List;

import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestionEvent;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class ConceptTreeNode extends Composite {

	private static QueryTreeNodeUiBinder uiBinder = GWT
			.create(QueryTreeNodeUiBinder.class);

	interface QueryTreeNodeUiBinder extends
			UiBinder<HorizontalPanel, ConceptTreeNode> {
	}

	private final OntologyServiceAsync ontologyService = GWT
			.create(OntologyService.class);

	@UiField
	OntologyElementSuggestBoxPanel suggestBoxPanel;
	@UiField
	Button addButton;

	TreeItem treeItem;

	public ConceptTreeNode(TreeItem treeItem) {
		initWidget(uiBinder.createAndBindUi(this));
		this.treeItem = treeItem;
		this.addButton.setEnabled(false);
		loadConceptNamesList();
	}

	@UiHandler("addButton")
	void handleClick(ClickEvent event) {
		TreeItem child = new TreeItem();
		child.setWidget(new PropertyTreeNode(child));
		treeItem.addItem(child);
		child.getTree().setSelectedItem(child);
		child.getTree().ensureSelectedItemVisible();
	}

	@UiHandler("suggestBoxPanel")
	void handleSelect(SelectionEvent<Suggestion> event) {
		this.addButton.setEnabled(true);
	}

	private void loadConceptNamesList() {
		AsyncCallbackWrapper<List<OntConcept>> callback = new AsyncCallbackWrapper<List<OntConcept>>() {

			@Override
			public void handleSuccess(List<OntConcept> result) {
				suggestBoxPanel.setSuggestions(result);
			}
		};
		callback.beforeCall();
		ontologyService.getConceptList(callback);
	}
}
