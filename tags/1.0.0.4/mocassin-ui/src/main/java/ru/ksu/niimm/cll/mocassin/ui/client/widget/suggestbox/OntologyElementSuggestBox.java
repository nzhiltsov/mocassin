package ru.ksu.niimm.cll.mocassin.ui.client.widget.suggestbox;

import java.util.Collection;

import ru.ksu.niimm.cll.mocassin.ui.client.OntElement;
import ru.ksu.niimm.cll.mocassin.ui.client.OntIndividual;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class OntologyElementSuggestBox extends SuggestBox implements
		FocusHandler {
	public OntologyElementSuggestBox(OntologyElementOracle oracle) {
		super(oracle);
		getTextBox().addFocusHandler(this);
	}

	@Override
	public void onFocus(FocusEvent event) {
		showSuggestionList();
	}

	public OntologyElementOracle getOracle() {
		return (OntologyElementOracle) getSuggestOracle();
	}

	/*
	 public OntElement getSelectedValue() {
		final String currentValue = getValue();
		Request request = new Request();
		SuggestionCallback callback = new SuggestionCallback(currentValue);
		getSuggestOracle().requestSuggestions(request, callback);
		return callback.getValue();
	}*/

	public static class SuggestionCallback implements Callback {
		private String suggestionString;
		private OntElement value;

		public SuggestionCallback(String suggestionString) {
			this.suggestionString = suggestionString;
		}

		public OntElement getValue() {
			return value;
		}

		public void setValue(OntElement value) {
			this.value = value;
		}

		public String getSuggestionString() {
			return suggestionString;
		}

		public void setSuggestionString(String suggestionString) {
			this.suggestionString = suggestionString;
		}

		@Override
		public void onSuggestionsReady(Request request, Response response) {
			Collection<? extends Suggestion> suggestions = response
					.getSuggestions();
			boolean elementFound = false;
			for (Suggestion suggestion : suggestions) {
				String suggestionString = suggestion.getReplacementString();
				if (suggestion instanceof OntologyElementSuggestion
						&& suggestionString.equals(getSuggestionString())) {
					OntologyElementSuggestion ontSuggestion = (OntologyElementSuggestion) suggestion;
					setValue(ontSuggestion.getOntologyElement());
					elementFound = true;
					break;
				}
			}
			if (!elementFound) { // the value is individual (instance) of
				// ontology element
				setValue(new OntIndividual(getSuggestionString(),
						getSuggestionString()));
			}
		}
	}
}
