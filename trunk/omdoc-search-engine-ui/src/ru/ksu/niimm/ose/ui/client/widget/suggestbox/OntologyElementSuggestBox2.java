package ru.ksu.niimm.ose.ui.client.widget.suggestbox;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.SuggestBox;

public class OntologyElementSuggestBox2 extends SuggestBox implements
		FocusHandler {

	public OntologyElementSuggestBox2(OntologyElementOracle oracle) {
		super(oracle);
		getTextBox().addFocusHandler(this);
	}

	@Override
	public void onFocus(FocusEvent event) {
		showSuggestionList();
	}

}
