/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.frontend.client;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.AsyncCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

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
		this.suggestBoxPanel.addSuggestBoxStyleName("concept-SuggestBox");

		boolean isRoot = this.treeItem.getParentItem() == null;
		if (isRoot) {
			loadConceptNamesList();
		} else {
			Widget parentItemWidget = this.treeItem.getParentItem().getWidget();
			if (parentItemWidget instanceof PropertyTreeNode) {
				PropertyTreeNode parentPropertyNode = (PropertyTreeNode) parentItemWidget;
				OntElement selectedOntElement = parentPropertyNode.suggestBoxPanel
						.getSelectedValue();
				loadRangeConceptList(selectedOntElement);
			}
		}

	}

	@UiHandler("addButton")
	void handleClick(ClickEvent event) {
		OntElement selectedConcept = suggestBoxPanel.getSelectedValue();
		TreeItem child = new TreeItem();
		treeItem.addItem(child);
		child.setWidget(new PropertyTreeNode(child, selectedConcept));
		child.getTree().setSelectedItem(child);
		child.getTree().ensureSelectedItemVisible();
	}

	@UiHandler("suggestBoxPanel")
	void handleSelect(SelectionEvent<Suggestion> event) {
		this.treeItem.removeItems();
		this.addButton.setEnabled(true);
	}

	private void loadConceptNamesList() {
		AsyncCallbackWrapper<List<OntConcept>> callback = new AsyncCallbackWrapper<List<OntConcept>>() {

			@Override
			public void handleSuccess(List<OntConcept> result) {
				suggestBoxPanel.setSuggestions(result);
			}
		};
		ontologyService.getConceptList(callback);
	}

	private void loadRangeConceptList(OntElement selectedOntologyElement) {
		AsyncCallbackWrapper<List<OntElement>> callback = new AsyncCallbackWrapper<List<OntElement>>() {

			@Override
			public void handleSuccess(List<OntElement> result) {
				suggestBoxPanel.setSuggestions(result);
			}
		};
		if (selectedOntologyElement instanceof OntRelation) {
			OntRelation selectedRelation = (OntRelation) selectedOntologyElement;
			ontologyService.getRelationRangeConceptList(selectedRelation,
					callback);
		}

	}
}
