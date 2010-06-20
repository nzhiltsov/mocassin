package ru.ksu.niimm.cll.mocassin.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TopPanel extends Composite {
	interface Binder extends UiBinder<VerticalPanel, TopPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private MocassinConstants constants = GWT.create(MocassinConstants.class);
	@UiField
	Label findLabel;
	@UiField
	QueryTree tree;
	@UiField
	Button sendButton;
	@UiField
	Button clearButton;
	@UiField
	InferenceCheckbox inferenceCheckbox;

	public TopPanel() {
		initWidget(binder.createAndBindUi(this));
		findLabel.setText(constants.findLabel());
		sendButton.setText(constants.sendButtonLabel());
		clearButton.setText(constants.clearButtonLabel());
	}

	@UiHandler("clearButton")
	void handleClick(ClickEvent event) {
		tree.clearTree();
	}

}
