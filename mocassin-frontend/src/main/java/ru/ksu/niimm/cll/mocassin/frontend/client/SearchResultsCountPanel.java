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
package ru.ksu.niimm.cll.mocassin.frontend.client;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.MocassinConstants;

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
