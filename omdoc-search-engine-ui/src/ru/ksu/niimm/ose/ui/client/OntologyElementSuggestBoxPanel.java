package ru.ksu.niimm.ose.ui.client;

import java.util.Collection;

import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementOracle;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class OntologyElementSuggestBoxPanel extends Composite {
	interface OntologyElementSuggestBoxUiBinder extends
			UiBinder<HorizontalPanel, OntologyElementSuggestBoxPanel> {
	}

	private static OntologyElementSuggestBoxUiBinder uiBinder = GWT
			.create(OntologyElementSuggestBoxUiBinder.class);

	private OntologyElementSuggestBox suggestBox;

	public OntologyElementSuggestBoxPanel() {
		this.suggestBox = new OntologyElementSuggestBox(
				new OntologyElementOracle());
		HorizontalPanel panel = uiBinder.createAndBindUi(this);
		panel.add(suggestBox);
		initWidget(panel);
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Suggestion> handler) {
		return suggestBox.addSelectionHandler(handler);
	}

	public void setSuggestions(Collection<? extends OntElement> ontElements) {
		suggestBox.getOracle().setOntologyElements(ontElements);
	}

}
