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
package ru.ksu.niimm.cll.mocassin.frontend.common.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingPanel extends Composite {
	interface Binder extends UiBinder<HorizontalPanel, LoadingPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private MocassinConstants constants = GWT.create(MocassinConstants.class);

	@UiField
	PopupPanel popup;
	@UiField
	Label title;

	public LoadingPanel() {
		initWidget(binder.createAndBindUi(this));
		popup.setModal(true);
		popup.setGlassEnabled(true);
		title.setText(constants.loadingTitleLabel());
	}

	public void popupShow() {
		popup.center();
	}

	public void popupHide() {
		popup.hide();
	}

}
