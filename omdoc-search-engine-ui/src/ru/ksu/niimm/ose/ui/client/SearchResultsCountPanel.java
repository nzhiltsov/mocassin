package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class SearchResultsCountPanel extends Composite {

	private static SearchResultsCountPanelUiBinder uiBinder = GWT
			.create(SearchResultsCountPanelUiBinder.class);

	interface SearchResultsCountPanelUiBinder extends
			UiBinder<HorizontalPanel, SearchResultsCountPanel> {
	}

	@UiField
	Label resultsLabel;

	public SearchResultsCountPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setSize(int size) {
		resultsLabel.setText(String.valueOf(size));
	}

}
