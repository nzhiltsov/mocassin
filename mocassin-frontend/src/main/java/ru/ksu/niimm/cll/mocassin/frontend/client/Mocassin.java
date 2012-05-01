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

import ru.ksu.niimm.cll.mocassin.frontend.client.CenterPanel.BuildQueryStatementHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mocassin implements EntryPoint {
	interface Binder extends UiBinder<ScrollPanel, Mocassin> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	TopPanel topPanel;
	@UiField
	CenterPanel centerPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ScrollPanel outer = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		root.forceLayout();
		topPanel.sendButton.addClickHandler(new BuildQueryStatementHandler(
				topPanel, centerPanel));
	}

}
