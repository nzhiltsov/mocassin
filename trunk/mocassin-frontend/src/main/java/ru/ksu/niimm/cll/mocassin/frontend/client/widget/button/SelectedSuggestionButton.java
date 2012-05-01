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
package ru.ksu.niimm.cll.mocassin.frontend.client.widget.button;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SelectedSuggestionButton extends Button implements
		SelectionHandler<Suggestion> {
	private Suggestion selectedItem;

	public SelectedSuggestionButton(String string) {
		super(string);
	}

	public Suggestion getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Suggestion selectedItem) {
		this.selectedItem = selectedItem;
	}

	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		Suggestion selectedItem = event.getSelectedItem();
		setSelectedItem(selectedItem);
	}

}
