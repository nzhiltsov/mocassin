package ru.ksu.niimm.ose.ui.client;

import java.util.List;

import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementOracle;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestBox;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

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
		loadPropertyList(domainConcept);
	}

	@UiHandler("suggestBoxPanel")
	void handleSelect(SelectionEvent<Suggestion> event) {
		Suggestion selectedItem = event.getSelectedItem();
		if (selectedItem instanceof OntologyElementSuggestion) {
			OntologyElementSuggestion ontSelectedItem = (OntologyElementSuggestion) selectedItem;
			OntElement selectedRelation = ontSelectedItem.getOntologyElement();
			loadRangeConceptList(selectedRelation);
		}

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
			callback.beforeCall();
			ontologyService.getRelationList(selectedConcept, callback);
		}

	}

	private void loadRangeConceptList(OntElement selectedOntologyElement) {
		AsyncCallbackWrapper<List<OntElement>> callback = new AsyncCallbackWrapper<List<OntElement>>() {

			@Override
			public void handleSuccess(List<OntElement> result) {
				TreeItem child = new TreeItem();
				child.setWidget(new ConceptTreeNode(child));
				treeItem.addItem(child);
				child.getTree().setSelectedItem(child);
				child.getTree().ensureSelectedItemVisible();
			}
		};
		if (selectedOntologyElement instanceof OntRelation) {
			OntRelation selectedRelation = (OntRelation) selectedOntologyElement;
			callback.beforeCall();
			ontologyService.getRelationRangeConceptList(selectedRelation,
					callback);
		}

	}
}
