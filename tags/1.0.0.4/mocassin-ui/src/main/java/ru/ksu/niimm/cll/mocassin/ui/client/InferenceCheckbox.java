package ru.ksu.niimm.cll.mocassin.ui.client;

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
		checkbox.setValue(true);
	}

	public boolean isChecked() {
		return checkbox.getValue();
	}

}
