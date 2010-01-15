package ru.ksu.niimm.ose.ui.client.widget.suggestbox;

import ru.ksu.niimm.ose.ui.client.OntElement;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

public class OntologyElementSuggestion extends MultiWordSuggestion {
	private OntElement ontologyElement;

	public OntologyElementSuggestion(OntElement ontologyElement) {
		this.ontologyElement = ontologyElement;
	}

	@Override
	public String getDisplayString() {
		return toString();
	}

	@Override
	public String getReplacementString() {
		return toString();
	}

	public OntElement getOntologyElement() {
		return ontologyElement;
	}

	@Override
	public String toString() {
		return isLabelEmpty() ? ontologyElement.getUri() : ontologyElement
				.getLabel();
	}

	private boolean isLabelEmpty() {
		return ontologyElement.getLabel() == null
				|| ontologyElement.getLabel().equals("");
	}
}
