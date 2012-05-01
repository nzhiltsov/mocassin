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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class InferenceCheckbox extends Composite {

	private static InferenceCheckboxUiBinder uiBinder = GWT
			.create(InferenceCheckboxUiBinder.class);
	private MocassinConstants constants = GWT.create(MocassinConstants.class);

	interface InferenceCheckboxUiBinder extends
			UiBinder<HorizontalPanel, InferenceCheckbox> {
	}

	@UiField
	CheckBox checkbox;

	public InferenceCheckbox() {
		initWidget(uiBinder.createAndBindUi(this));
		checkbox.setText(constants.inferenceCheckboxLabel());
		checkbox.setValue(false);
	}

	public boolean isChecked() {
		return checkbox.getValue();
	}

}
