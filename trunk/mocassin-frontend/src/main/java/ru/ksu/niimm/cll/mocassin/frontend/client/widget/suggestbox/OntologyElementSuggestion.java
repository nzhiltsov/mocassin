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
package ru.ksu.niimm.cll.mocassin.frontend.client.widget.suggestbox;

import ru.ksu.niimm.cll.mocassin.frontend.client.OntElement;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

public class OntologyElementSuggestion extends MultiWordSuggestion implements
		Comparable<OntologyElementSuggestion> {
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

	@Override
	public int compareTo(OntologyElementSuggestion suggestion) {
		if (suggestion == null)
			return 0;
		return this.ontologyElement.getLabel().compareTo(
				suggestion.getOntologyElement().getLabel());
	}

}
