package ru.ksu.niimm.cll.mocassin.frontend.client;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.AsyncCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class PropertyTreeNode extends Composite {

	private static PropertyTreeNodeUiBinder uiBinder = GWT
			.create(PropertyTreeNodeUiBinder.class);

	interface PropertyTreeNodeUiBinder extends
			UiBinder<Widget, PropertyTreeNode> {
	}

	private final OntologyServiceAsync ontologyService = GWT
			.create(OntologyService.class);
	@UiField
	OntologyElementSuggestBoxPanel suggestBoxPanel;

	TreeItem treeItem;

	public PropertyTreeNode(TreeItem treeItem, OntElement domainConcept) {
		initWidget(uiBinder.createAndBindUi(this));
		this.treeItem = treeItem;
		this.suggestBoxPanel.addSuggestBoxStyleName("property-SuggestBox");
		loadPropertyList(domainConcept);
	}

	@UiHandler("suggestBoxPanel")
	void handleSelect(SelectionEvent<Suggestion> event) {
		treeItem.removeItems();
		TreeItem child = new TreeItem();
		treeItem.addItem(child);
		child.setWidget(new ConceptTreeNode(child));
		child.getTree().setSelectedItem(child);
		child.getTree().ensureSelectedItemVisible();

	}

	private void loadPropertyList(OntElement selectedOntologyElement) {
		AsyncCallbackWrapper<List<OntRelation>> callback = new AsyncCallbackWrapper<List<OntRelation>>() {

			@Override
			public void handleSuccess(List<OntRelation> result) {
				suggestBoxPanel.setSuggestions(result);
			}
		};
		if (selectedOntologyElement instanceof OntConcept) {
			OntConcept selectedConcept = (OntConcept) selectedOntologyElement;
			ontologyService.getRelationList(selectedConcept, callback);
		}

	}

}
