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

import java.util.Collection;

import ru.ksu.niimm.cll.mocassin.frontend.client.widget.suggestbox.OntologyElementOracle;
import ru.ksu.niimm.cll.mocassin.frontend.client.widget.suggestbox.OntologyElementSuggestBox;
import ru.ksu.niimm.cll.mocassin.frontend.client.widget.suggestbox.OntologyElementSuggestion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class OntologyElementSuggestBoxPanel extends Composite implements
		SelectionHandler<Suggestion> {
	interface OntologyElementSuggestBoxUiBinder extends
			UiBinder<HorizontalPanel, OntologyElementSuggestBoxPanel> {
	}

	private static OntologyElementSuggestBoxUiBinder uiBinder = GWT
			.create(OntologyElementSuggestBoxUiBinder.class);

	private OntologyElementSuggestBox suggestBox;

	private OntElement selectedElement;

	public OntologyElementSuggestBoxPanel() {
		this.suggestBox = new OntologyElementSuggestBox(
				new OntologyElementOracle());
		HorizontalPanel panel = uiBinder.createAndBindUi(this);
		panel.add(suggestBox);
		suggestBox.addSelectionHandler(this);
		initWidget(panel);
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Suggestion> handler) {
		return suggestBox.addSelectionHandler(handler);
	}

	public void setSuggestions(Collection<? extends OntElement> ontElements) {
		suggestBox.getOracle().setOntologyElements(ontElements);
	}

	public OntElement getSelectedValue() {
		String text = suggestBox.getText();
		if (this.selectedElement == null) {
			this.selectedElement = text == null || text.equals("") ? new OntBlankNode()
					: new OntLiteral(text);
		} else if (this.selectedElement instanceof OntLiteral) {
			this.selectedElement.setLabel(text);
		}
		return this.selectedElement;
	}

	public void addSuggestBoxStyleName(String style) {
		suggestBox.addStyleName(style);
	}

	public void showSuggestionList() {
		suggestBox.showSuggestionList();
	}

	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		Suggestion selectedItem = event.getSelectedItem();
		if (selectedItem instanceof OntologyElementSuggestion) {

			OntologyElementSuggestion selectedOntSuggestion = (OntologyElementSuggestion) selectedItem;
			this.selectedElement = selectedOntSuggestion.getOntologyElement();
		}
	}

}
