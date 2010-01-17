package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public class OntologyElementSuggestBox extends Composite {

	private static OntologyElementSuggestBoxUiBinder uiBinder = GWT
			.create(OntologyElementSuggestBoxUiBinder.class);

	interface OntologyElementSuggestBoxUiBinder extends
			UiBinder<SuggestBox, OntologyElementSuggestBox> {
	}

	public OntologyElementSuggestBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
