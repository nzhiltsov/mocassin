package ru.ksu.niimm.cll.mocassin.frontend.client.widget.suggestbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.client.OntElement;

import com.google.gwt.user.client.ui.SuggestOracle;

public class OntologyElementOracle extends SuggestOracle {

	private static final int MIN_QUERY_STRING_LENGTH = 0;
	private static final int MAX_SHOWN_SUGGESTIONS_COUNT = 20;
	private List<OntologyElementSuggestion> elementSuggestions;

	public void setOntologyElements(Collection<? extends OntElement> ontElements) {
		elementSuggestions = new ArrayList<OntologyElementSuggestion>();
		for (OntElement element : ontElements) {
			OntologyElementSuggestion suggestion = new OntologyElementSuggestion(
					element);
			elementSuggestions.add(suggestion);
		}
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		Response response = new Response(matchingElements(request.getQuery()));
		callback.onSuggestionsReady(request, response);
	}

	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {
		List<OntologyElementSuggestion> responseSuggestions = new ArrayList<OntologyElementSuggestion>();
		if ((request.getQuery() == null || request.getQuery().length() == 0)
				&& getElementSuggestions().size() <= MAX_SHOWN_SUGGESTIONS_COUNT) {
			for (OntologyElementSuggestion elementSuggestion : getElementSuggestions()) {
				responseSuggestions.add(elementSuggestion);
			}
		}
		Response response = new Response(responseSuggestions);
		callback.onSuggestionsReady(request, response);
	}

	private Collection<OntologyElementSuggestion> matchingElements(String query) {
		List<OntologyElementSuggestion> matchingSuggestions = new ArrayList<OntologyElementSuggestion>();
		if (query.length() > MIN_QUERY_STRING_LENGTH) {
			String prefixToMatch = query.toLowerCase();
			for (OntologyElementSuggestion elementSuggestion : getElementSuggestions()) {
				if (elementSuggestion.getDisplayString().toLowerCase()
						.startsWith(prefixToMatch)) {
					matchingSuggestions.add(elementSuggestion);
				}

			}
		}
		return matchingSuggestions;
	}

	public List<OntologyElementSuggestion> getElementSuggestions() {
		return elementSuggestions;
	}

}
