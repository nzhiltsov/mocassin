package ru.ksu.niimm.ose.ui.client.widget.suggestbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.ksu.niimm.ose.ui.client.OntElement;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class OntologyElementOracle extends MultiWordSuggestOracle {

	public void setOntologyElements(Collection<? extends OntElement> ontElements) {
		List<Suggestion> suggestions = new ArrayList<Suggestion>();
		for (OntElement element : ontElements) {
			OntologyElementSuggestion suggestion = new OntologyElementSuggestion(
					element);
			suggestions.add(suggestion);
		}
		setDefaultSuggestions(suggestions);
	}
}
