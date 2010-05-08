package ru.ksu.niimm.cll.mocassin.ui.client;

import java.util.Collection;

import ru.ksu.niimm.cll.mocassin.ui.client.widget.suggestbox.OntologyElementOracle;
import ru.ksu.niimm.cll.mocassin.ui.client.widget.suggestbox.OntologyElementSuggestBox;
import ru.ksu.niimm.cll.mocassin.ui.client.widget.suggestbox.OntologyElementSuggestion;

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
		if (this.selectedElement == null) {
			this.selectedElement = new OntLiteral(suggestBox.getText());
		} else if (this.selectedElement instanceof OntLiteral) {
			this.selectedElement.setLabel(suggestBox.getText());
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
