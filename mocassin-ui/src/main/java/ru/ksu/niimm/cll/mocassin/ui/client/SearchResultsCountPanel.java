package ru.ksu.niimm.cll.mocassin.ui.client;

import ru.ksu.niimm.cll.mocassin.ui.common.client.MocassinConstants;

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

	private MocassinConstants constants = GWT.create(MocassinConstants.class);

	@UiField
	Label resultTitleLabel;
	@UiField
	Label countLabel;

	public SearchResultsCountPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		resultTitleLabel.setText(constants.resultsTitleLabel());
	}

	public void setSize(int size) {
		countLabel.setText(String.valueOf(size));
	}

	public Label getResultTitleLabel() {
		return resultTitleLabel;
	}

}
